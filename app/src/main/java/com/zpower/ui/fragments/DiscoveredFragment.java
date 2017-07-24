package com.zpower.ui.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.service.MainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zx on 2017/2/25.
 */

public class DiscoveredFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DiscoveredFragment";
    private ImageView iv_back;
    private View rootView;
    private RelativeLayout relativeLayout;
    private BluetoothDevice device;
    private TextView tv_device_name;
    private ProgressDialog progressDialog;
    private TextView tv_device_mac;
    public static DiscoveredFragment newInstance(){
        return new DiscoveredFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discovered,container,false);
        initView();
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initView() {
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.RL_discovered_device);
        tv_device_name = (TextView) rootView.findViewById(R.id.tv_device_name);
        tv_device_mac = (TextView) rootView.findViewById(R.id.tv_device_mac);
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
            case R.id.RL_discovered_device:
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setMessage("正在连接设备:"+device.getName());
                progressDialog.show();
                //在子线程中连接蓝牙设备
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MainService.getService().connectBLEDevice(device);
                        }
                    }).start();
                break;
        }
    }


    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
//        super.onBluetoothConnect(device);
        Log.e(TAG,device.getAddress()+"连接成功");
        Toast.makeText(getActivity(),"连接成功！", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().postSticky(device.getName());
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        start(ConnectedFragment.newInstance());
    }
    @Override
    public void onBluetoothDisconnect() {
//        super.onBluetoothDisconnect();
        Toast.makeText(getActivity(),"连接失败请重试！", Toast.LENGTH_SHORT).show();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBluetoothConnecting() {
//        super.onBluetoothConnecting();
        Toast.makeText(getActivity(),"正在连接...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBluetoothDsiconnecting() {
//        super.onBluetoothDsiconnecting();
        Toast.makeText(getActivity(),"正在断开连接", Toast.LENGTH_LONG).show();

    }

    /***
     * 接收Eventbus消息
     * @param device
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventReceiver(BluetoothDevice device){
        this.device = device;
        tv_device_name.setText(device.getName());
        tv_device_mac.setText(device.getAddress());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
