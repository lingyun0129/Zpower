package com.zpower.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zpower.GlobalVars;
import com.zpower.MessageTypes;
import com.zpower.R;
import com.zpower.model.BluetoothUUID;
import com.zpower.service.BluetoothService;
import com.zpower.utils.MyLog;

import java.util.Arrays;
import java.util.List;

/**
 * 处理蓝牙的管理工作，提供相关管理接口
 * 不允许UI层直接使用
 * 注：相关方法根据实际需要添加
 *
 * @author guzhicheng
 */
public class MyBluetoothManager {
    private final static String TAG = MyBluetoothManager.class.getCanonicalName();
    private static final long SCAN_PERIOD = 10 * 1000;
    //懒汉单例模式（双重检查锁定保证线程安全）
    private static MyBluetoothManager mInstance = null;

    private boolean isConnected = false;
    private boolean mScanning = false;
    private MyBluetoothManager() {
        Log.i(TAG, "BluetoothService()");
    }

    /**
     * 获取蓝牙管理服务实例
     *
     * @return
     */
    public static MyBluetoothManager getInstance() {
        if (mInstance == null) {
            synchronized (MyBluetoothManager.class) {
                if (mInstance == null) {
                    mInstance = new MyBluetoothManager();
                    //FileUtils.init();
                }
            }
        }
        return mInstance;
    }
    /**
     * 蓝牙适配器
     * BluetoothAdapter是Android系统中所有蓝牙操作都需要的，
     * 它对应本地Android设备的蓝牙模块，
     * 在整个系统中BluetoothAdapter是单例的。
     * 当你获取到它的实例之后，就能进行相关的蓝牙操作了。
     */
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /***
     * 检查蓝牙是否打开
     *
     * @param context
     * @return
     */
    public void checkDevice(Activity context) {

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            context.finish();
            return;
        }
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            context.finish();
            return;
        }
        //检查蓝牙是否打开
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(intent);
            }
        }
    }

    /**
     * Location service if enable
     *
     * @param context
     * @return location is enable if return true, otherwise disable.
     */
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    /***
     * 开始扫描设备
     */
    public void startDiscoveringDevices() {
/*        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();*/
       scanLeDevice(true);
    }

    /**
     * 扫描低功率蓝牙外设
     *
     * @param enable
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            MyLog.e(TAG, "find a new device name:" + device.getName() + " mac:" + device.getAddress());
            onRegisterBTReceiver.onBluetoothNewDevice(device);//发现新设备
        }
    };

    public void scanLeDevice(final boolean enable) {
        Handler mHandler = new Handler();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    onRegisterBTReceiver.onDiscoveryFinished();//停掉扫描动画
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * 蓝牙状态接口
     */
    private OnRegisterBTReceiver onRegisterBTReceiver;

    public interface OnRegisterBTReceiver {
        void onBluetoothNewDevice(BluetoothDevice device);//搜索到新设备

        void onBluetoothPairing(BluetoothDevice device);//配对中

        void onBluetoothPaired(BluetoothDevice device);//配对完成

        void onBluetoothUnpaired(BluetoothDevice device);//取消配对

        void onDiscoveryFinished();//扫描完成
    }

    /**
     * 注册广播来接收蓝牙配对信息
     *
     * @param context
     */
    public void
    registerBTReceiver(Context context, OnRegisterBTReceiver onRegisterBTReceiver) {
        this.onRegisterBTReceiver = onRegisterBTReceiver;
        // 用BroadcastReceiver来取得搜索结果
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//结束
        context.registerReceiver(myBluetoothReceiver, intent);
        MyLog.e(TAG, "registerBTReceiver");
    }

    /**
     * 反注册广播取消蓝牙的配对
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        if (myBluetoothReceiver != null) {
            context.unregisterReceiver(myBluetoothReceiver);
        }
        if (getmBluetoothAdapter() != null)
            //getmBluetoothAdapter().cancelDiscovery();
            scanLeDevice(false);
    }

    /**
     * 蓝牙接收广播
     */
    private BroadcastReceiver myBluetoothReceiver = new BroadcastReceiver() {
        //接收
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.e(TAG, action);

            BluetoothDevice device;
            // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                MyLog.e(TAG, "=============================");
                MyLog.e(TAG, "DeviceName:" + device.getName());
                MyLog.e(TAG, "DeviceAddress:" + device.getAddress());
                onRegisterBTReceiver.onBluetoothNewDevice(device);
            }
            //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                MyLog.e(TAG, "==============Status===========");
                MyLog.e(TAG, "Name:" + device.getName());
                MyLog.e(TAG, "Status:" + device.getBondState());
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对回调
                        onRegisterBTReceiver.onBluetoothPairing(device);
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束回调
                        onRegisterBTReceiver.onBluetoothPaired(device);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对回调
                        onRegisterBTReceiver.onBluetoothUnpaired(device);
                    default:
                        break;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                onRegisterBTReceiver.onDiscoveryFinished();
            }
        }
    };


    private BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic indoor_bike_data_characteristic;
    BluetoothGattCharacteristic status_characteristic;
    BluetoothGattCharacteristic control_point_characteristic;

    /**
     * 尝试配对和连接
     *
     * @param device
     */
    public void createBond(final BluetoothDevice device, final Handler handler) {
        mGattCallback.handler = handler;
        mGattCallback.device = device;
        //连接蓝牙
        mBluetoothGatt = device.connectGatt(GlobalVars.mMainActivity, true, mGattCallback);
    }

    private MyBluetoothGattCallback mGattCallback = new MyBluetoothGattCallback();

    class MyBluetoothGattCallback extends BluetoothGattCallback {

        public Handler handler;
        public BluetoothDevice device;

        //当连接上设备或者失去连接时会回调该函数
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            MyLog.e(TAG, "Status:" + status + "  newSatte:" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {//连接成功
                MyLog.e(TAG, "Status--STATE_CONNECTED");
                mBluetoothGatt = gatt;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.discoverServices();//连接成功后就去找出该设备中的服务
                    }
                }, 500);
                if (!isConnected) {
                    Message msg = handler.obtainMessage(MessageTypes.CONNECT_STATE_CONNECTED);
                    msg.obj = device;
                    msg.sendToTarget();
                    isConnected = true;
                }

            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                MyLog.e(TAG, "Status--STATE_CONNECTING");
                handler.sendEmptyMessage(MessageTypes.CONNECT_STATE_CONNECTING);

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                MyLog.e(TAG, "Status--STATE_DISCONNECTED");
                handler.sendEmptyMessage(MessageTypes.CONNECT_STATE_DISCONNECTED);
                isConnected = false;
                BluetoothService.stopReadData();

            } else if (newState == BluetoothGatt.STATE_DISCONNECTING) {
                MyLog.e(TAG, "Status--STATE_DISCONNECTING");
                handler.sendEmptyMessage(MessageTypes.CONNECT_STATE_DISCONNECTING);

            }
        }

        @Override//当设备是否找到服务时，会回调该函数
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                        super.onServicesDiscovered(gatt, status);
            MyLog.e(TAG, "onServicesDiscovered(" + status + ")");
            int i=0,j=0;
            List<BluetoothGattService> serviceList=gatt.getServices();
            for(BluetoothGattService service:serviceList){
                MyLog.e(TAG,"service "+(++i)+" UUID="+service.getUuid().toString());
                List<BluetoothGattCharacteristic> charList=service.getCharacteristics();
                j=0;
                for (BluetoothGattCharacteristic charac:charList){
                    MyLog.e(TAG,"Characteristic "+(++j)+" UUID="+charac.getUuid().toString());
                }
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            //找到服务了
            mBluetoothGatt = gatt;
            BluetoothGattService service = mBluetoothGatt.getService(BluetoothUUID.FITNESS_MACHINE_SERVICE);
            if (service == null) {
                MyLog.e(TAG, "Service is null");
                return;
            }

            indoor_bike_data_characteristic = service.getCharacteristic(BluetoothUUID.INDOOR_BIKE_DATA);

            if (indoor_bike_data_characteristic == null) {
                MyLog.e(TAG, "indoor bike data Chara is null.");
                return;
            }
            BluetoothGattDescriptor descriptor = indoor_bike_data_characteristic.getDescriptor(BluetoothUUID.DESCR);
            if (descriptor == null) {
                MyLog.e(TAG, "descriptor is null");
                return;
            }
            gatt.setCharacteristicNotification(indoor_bike_data_characteristic, true);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);

            //status
            status_characteristic=service.getCharacteristic(BluetoothUUID.FITNESS_MACHINE_STATUS);
            BluetoothGattDescriptor status_descriptor = status_characteristic.getDescriptor(BluetoothUUID.DESCR);
            gatt.setCharacteristicNotification(status_characteristic, true);
            status_descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(status_descriptor);

            //control point
            control_point_characteristic=service.getCharacteristic(BluetoothUUID.FITNESS_MACHINE_CONTROL_POINT);
            BluetoothGattDescriptor control_point_descriptor = control_point_characteristic.getDescriptor(BluetoothUUID.DESCR);
            control_point_descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(control_point_descriptor);

            MyLog.e(TAG, "setCharacteristicNotification");
        }

        @Override//当读取设备时会回调该函数
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            MyLog.e(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。然后再进行解析操作。
                //int charaProp = characteristic.getProperties();
                // if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                // 表示可发出通知。  判断该Characteristic属性
            }
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override//设备发出通知时会调用到该接口
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            MyLog.e(TAG, "onCharacteristicChanged");
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                MyLog.e(TAG,"接收到的数据:"+stringBuilder.toString());
            }
            //save data to file
/*            if(data.length>2){
                FileUtils.saveBytesToFile(data);
            }*/
            BluetoothService.handlerBlueData(data);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            MyLog.e(TAG, "onCharacteristicWrite uuid="+characteristic.getUuid().toString());
            byte[]data=characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                MyLog.e(TAG,"写操作响应的数据:"+stringBuilder.toString()+" status="+status);
            }

            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    }


    //写入
    public boolean writeCharacteristic(byte[] value) {
        Log.e(TAG, "value:" + Arrays.toString(value));
        //check mBluetoothGatt is available
        if (mBluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
        BluetoothGattService Service = mBluetoothGatt.getService(BluetoothUUID.FITNESS_MACHINE_SERVICE);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            return false;
        }

        BluetoothGattCharacteristic charac = Service
                .getCharacteristic(BluetoothUUID.FITNESS_MACHINE_CONTROL_POINT);
        if (charac == null) {
            Log.e(TAG, "char not found!");
            return false;
        }
        charac.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(charac);
        return status;
    }
}
