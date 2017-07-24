package com.vogtec.cr01.bluetooth;

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vogtec.cr01.GlobalVars;
import com.vogtec.cr01.MessageTypes;
import com.vogtec.cr01.model.BluetoothUUID;
import com.vogtec.cr01.service.BluetoothService;
import com.vogtec.cr01.utils.MyLog;

import java.util.Arrays;

/**
 * 处理蓝牙的管理工作，提供相关管理接口
 * 不允许UI层直接使用
 * 注：相关方法根据实际需要添加
 *
 * @author guzhicheng
 */
public class MyBluetoothManager {
    private final static String TAG = MyBluetoothManager.class.getCanonicalName();
    //单例模式
    private static MyBluetoothManager mInstance;

    private boolean isConnected = false;

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
            mInstance = new MyBluetoothManager();
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
    public void checkDevice(Context context) {

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(intent);
            }
        }
    }

    /***
     * 开始扫描设备
     */
    public void startDiscoveringDevices() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        MyLog.e(TAG, "startDiscoveringDevices");
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
        if(mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        if(myBluetoothReceiver != null){
            context.unregisterReceiver(myBluetoothReceiver);
        }
        if (getmBluetoothAdapter() != null)
            getmBluetoothAdapter().cancelDiscovery();
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
    BluetoothGattCharacteristic characteristic;

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

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            MyLog.e(TAG, "Status:" + status + "  newSatte:" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
                MyLog.e(TAG, "Status--STATE_CONNECTED");
                mBluetoothGatt = gatt;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.discoverServices();
                    }
                }, 500);
                if(!isConnected) {
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

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                        super.onServicesDiscovered(gatt, status);
            MyLog.e(TAG, "onServicesDiscovered(" + status + ")");
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            mBluetoothGatt = gatt;
            BluetoothGattService service = mBluetoothGatt.getService(BluetoothUUID.SERVICE);
            if (service == null) {
                MyLog.e(TAG, "Service is null");
                return;
            }
            characteristic = service.getCharacteristic(BluetoothUUID.NOTIFICATION1);
            if (characteristic == null) {
                MyLog.e(TAG, "Chara is null.");
                return;
            }
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BluetoothUUID.DESCR);
            if (descriptor == null) {
                MyLog.e(TAG, "descriptor is null");
                return;
            }
            gatt.setCharacteristicNotification(characteristic, true);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            MyLog.e(TAG, "setCharacteristicNotification");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            MyLog.e(TAG, "onCharacteristicRead");
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
          MyLog.e(TAG, "onCharacteristicChanged");
//            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            BluetoothService.handlerBlueData(data);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            MyLog.e(TAG, "onCharacteristicWrite");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    }
    //写入
    public boolean writeCharacteristic(byte[] value) {
        Log.e(TAG,"value:"+Arrays.toString(value));
        //check mBluetoothGatt is available
        if (mBluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
        BluetoothGattService Service = mBluetoothGatt.getService(BluetoothUUID.WRITE_SERVICE);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            return false;
        }
        BluetoothGattCharacteristic charac = Service
                .getCharacteristic(BluetoothUUID.WRITE);
        if (charac == null) {
            Log.e(TAG, "char not found!");
            return false;
        }
        charac.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(charac);
        return status;
    }
}
