package com.zpone.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.zpone.R;
import com.zpone.model.BluetoothUUID;
import com.zpone.utils.BaseUtils;

import java.util.Arrays;
import java.util.List;

public class ConnectedDeviceInfoFragment extends BaseFragment {
    View rootView;
    BleDevice connected_device=null;
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
        Button vibrate=(Button)rootView.findViewById(R.id.btn_vibrate);
        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected_device!=null){
                    writeCharacteristic(new byte[]{0x68,0x01,0x03,0x6c});
                }
            }
        });
        List<BleDevice> connected_devices=BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice device:connected_devices) {
            connected_device=device;
            device_name.setText(device.getName());
            device_mac.setText(device.getMac());
            device_rssi.setText(device.getRssi()+"");
            break;
        }

    }

    public boolean writeCharacteristic(byte[] value){
        final boolean[] result = {false};
        BleManager.getInstance().write(connected_device,
                BluetoothUUID.RING_SERVICE.toString(),
                BluetoothUUID.RING_WRITE_CHAR.toString(),
                value,
                new BleWriteCallback(){

                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {
                        Toast.makeText(getActivity(),"写入成功:"+ Integer.toHexString(BaseUtils.bytes4ToInt(bytes,0)),Toast.LENGTH_SHORT).show();
                        result[0] =true;
                    }

                    @Override
                    public void onWriteFailure(BleException e) {
                        Toast.makeText(getActivity(),"写入失败",Toast.LENGTH_SHORT).show();
                        result[0] =false;
                    }
                });
        return result[0];
    }
}
