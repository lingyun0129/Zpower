package com.zpone.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ReplacementTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.zpone.R;
import com.zpone.bluetooth.MyBluetoothManager;
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

    public static OpenBluetoothFragment newInstance() {
        return new OpenBluetoothFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_open_bluetooth, container, false);
        //myBluetoothManager.initBluetoothManager(getActivity());
        initView();
        initData();
        return rootView;
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
            case R.id.btn_mac_connect:
                break;
        }
    }

    private void initData() {
        //检查蓝牙是否打开
        myBluetoothManager.checkDevice(getActivity());
        //registerMyBTReceiver();
        MyLog.e(TAG, "myBluetoothAdapter.isEnabled:" + myBluetoothAdapter.isEnabled());
        if (myBluetoothAdapter.isEnabled()) {
            lodingIndicator.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission();
            }
            //myBluetoothManager.startDiscoveringDevices();
        } else {
            lodingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void requestPermission() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA}, 2);
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

    private void registerMyBTReceiver() {
        myBluetoothManager.registerBTReceiver(getActivity(), new MyBluetoothManager.OnRegisterBTReceiver() {

            /***
             * 发现新设备
             * @param device
             */
            @Override
            public void onBluetoothNewDevice(BluetoothDevice device) {
                if (device != null && device.getName() != null) {
                    //nrf51422_HRM E7:9B:EE:4B:9C:79
                    //E3:B1:08:D7:12:E5
                    /*if (device.getAddress().equals("DC:C5:0A:7D:1C:AC")||device.getAddress().equals("E2:0A:F4:68:E4:9D")||device.getAddress().equals("E9:BC:4E:A5:DB:AE"))*/
                    {
                        //myBluetoothAdapter.cancelDiscovery();
                        //myBluetoothManager.scanLeDevice(false);
                        //start(DiscoveredFragment2.newInstance());//跳转到DiscoveredFragment
                        //EventBus.getDefault().postSticky(device);
                    }
                }

            }

            @Override
            public void onBluetoothPairing(BluetoothDevice device) {

            }

            @Override
            public void onBluetoothPaired(BluetoothDevice device) {

            }

            @Override
            public void onBluetoothUnpaired(BluetoothDevice device) {

            }

            @Override
            public void onDiscoveryFinished() {
                lodingIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //myBluetoothManager.unregisterReceiver(getActivity());
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
