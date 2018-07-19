package com.zpone.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.wang.avi.AVLoadingIndicatorView;
import com.zpone.R;
import com.zpone.bluetooth.MyBluetoothManager;
import com.zpone.observer.ObserverManager;
import com.zpone.utils.KeyBoardUtils;
import com.zpone.utils.MyLog;
import com.zpone.zxing.fragment.CaptureFragment;

import static com.zpone.R.id.iv_back;

/**
 * Created by zx on 2017/2/22.
 */

public class OpenBluetoothFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = OpenBluetoothFragment.class.getCanonicalName();
    private View rootView;
    private EditText et_mac;
    MyBluetoothManager myBluetoothManager = MyBluetoothManager.getInstance();
    BluetoothAdapter myBluetoothAdapter = myBluetoothManager.getmBluetoothAdapter();
    private AVLoadingIndicatorView lodingIndicator;
    private ProgressDialog progressDialog = null;

    public static OpenBluetoothFragment newInstance() {
        return new OpenBluetoothFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_open_bluetooth, container, false);
        initView();
        initData();
        initBLEManager();
        return rootView;
    }

    private void initBLEManager() {
        BleManager.getInstance().init(getActivity().getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);
    }

    private void initView() {
        lodingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.lodaingView);
        //ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        et_mac = (EditText) rootView.findViewById(R.id.et_mac);
        et_mac.setTransformationMethod(new UpperCaseTransform());
        Button mac_connect = (Button) rootView.findViewById(R.id.btn_mac_connect);
        Button btn_rescan = (Button) rootView.findViewById(R.id.btn_scan);
        Button btn_search = (Button) rootView.findViewById(R.id.btn_search);

        //iv_back.setOnClickListener(this);
        mac_connect.setOnClickListener(this);
        btn_rescan.setOnClickListener(this);
        btn_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case iv_back:
                pop();
                break;
            //搜索
            case R.id.btn_search:
                if (lodingIndicator.getVisibility() == View.INVISIBLE) {
                    lodingIndicator.setVisibility(View.VISIBLE);
                    initData();
                }
                myBluetoothManager.startDiscoveringDevices();
                start(DiscoveredFragment2.newInstance());//跳转到DiscoveredFragment
                break;
            //扫二维码
            case R.id.btn_scan: {
                start(CaptureFragment.newInstance());
            }
            break;
            //mac地址连接
            case R.id.btn_mac_connect: {
                KeyBoardUtils.hideKeyboard(et_mac);
                final String mac_str = et_mac.getText().toString().toUpperCase();
                BluetoothAdapter bluetoothAdapter = BleManager.getInstance().getBluetoothAdapter();
                if (bluetoothAdapter != null && BluetoothAdapter.checkBluetoothAddress(mac_str)) {
                    final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac_str);
                    if (device != null) {
                        //在子线程中连接蓝牙设备
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(true);
                        progressDialog.setCanceledOnTouchOutside(true);
                        progressDialog.setMessage("Connecting:" + device.getName());
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //MainService.getService().connectBLEDevice(device);
                                Looper.prepare();
                                BleManager.getInstance().connect(mac_str, new MyBleGattCallback());
                                Looper.loop();
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.invalid_mac_adress, Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void initData() {
        //检查蓝牙是否打开
        myBluetoothManager.checkDevice(getActivity());
        MyLog.e(TAG, "myBluetoothAdapter.isEnabled:" + myBluetoothAdapter.isEnabled());
        if (myBluetoothAdapter.isEnabled()) {
            //lodingIndicator.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission();
            }
        } else {
            lodingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void requestPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA}, 2);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(getActivity(), R.string.location_permission_request, Toast.LENGTH_SHORT).show();
            }
        }
        //检查位置信息是否打开
        if (!myBluetoothManager.isLocationEnable(getActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.open_location_service);
            builder.setMessage(R.string.location_permission_request);
            builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivityForResult(locationIntent, 2);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        Log.e("cly","OpenBluetoothFragment OnDestroy is called");
        super.onDestroy();
    }

    /**
     * 连接回调
     */
    class MyBleGattCallback extends BleGattCallback {

        @Override
        public void onStartConnect() {
            Toast.makeText(getActivity(), "正在连接！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            start(ConnectedDeviceInfoFragment.newInstance());
            Toast.makeText(getActivity(), "连接成功：！" + bleDevice.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "连接断开！", Toast.LENGTH_SHORT).show();
            }
            ObserverManager.getInstance().notifyObserver(bleDevice);
        }
    }

    /**
     * 小写转大写
     */
    class UpperCaseTransform extends ReplacementTransformationMethod {
        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] AA = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return AA;
        }
    }

}
