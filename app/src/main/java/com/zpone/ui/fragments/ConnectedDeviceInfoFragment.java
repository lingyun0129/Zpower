package com.zpone.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.zpone.R;

import java.util.List;

public class ConnectedDeviceInfoFragment extends BaseFragment {
    View rootView;
    public static ConnectedDeviceInfoFragment newInstance(){
        return new ConnectedDeviceInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connected_device_info,container,false);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        initView();
        return rootView;
    }

    private void initView() {
        TextView device_name,device_mac,device_rssi;
        device_name=(TextView)rootView.findViewById(R.id.device_name);
        device_mac=(TextView)rootView.findViewById(R.id.device_mac);
        device_rssi=(TextView)rootView.findViewById(R.id.device_rssi);
        List<BleDevice> connected_devices=BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice device:connected_devices) {
            device_name.setText(device.getName());
            device_mac.setText(device.getMac());
            device_rssi.setText(device.getRssi()+"");
            break;
        }
    }
}
