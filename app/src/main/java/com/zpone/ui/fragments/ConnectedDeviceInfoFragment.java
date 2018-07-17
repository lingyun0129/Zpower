package com.zpone.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.zpone.R;
import com.zpone.model.BluetoothUUID;
import com.zpone.observer.Observer;
import com.zpone.observer.ObserverManager;

import java.util.List;

public class ConnectedDeviceInfoFragment extends BaseFragment implements Observer, View.OnClickListener {
    View rootView;
    TextView parking_status, network_status, battery_info;
    Button test_mode, test_park, test_set_lmd, test_set_time, test_highlevel_time, test_lowlevel_time, test_get_battery, test_normal;
    ImageView iv_back;
    BleDevice connected_device = null;

    public static ConnectedDeviceInfoFragment newInstance() {
        return new ConnectedDeviceInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connected_device_info, container, false);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        initView();
        ObserverManager.getInstance().addObserver(this);
        return rootView;
    }

    private void initView() {
        //TextView device_name, device_mac;
        //device_name = (TextView) rootView.findViewById(R.id.device_name);
        //device_mac = (TextView) rootView.findViewById(R.id.device_mac);
        test_mode = (Button) rootView.findViewById(R.id.btn_test_mode);
        test_mode.setOnClickListener(this);
        test_park = (Button) rootView.findViewById(R.id.btn_test_park);
        test_park.setOnClickListener(this);
        test_set_lmd = (Button) rootView.findViewById(R.id.btn_test_lmd);
        test_set_lmd.setOnClickListener(this);
        test_set_time = (Button) rootView.findViewById(R.id.btn_test_time);
        test_set_time.setOnClickListener(this);
        test_highlevel_time = (Button) rootView.findViewById(R.id.btn_test_highlevel_time);
        test_highlevel_time.setOnClickListener(this);
        test_lowlevel_time = (Button) rootView.findViewById(R.id.btn_test_lowlevel_time);
        test_lowlevel_time.setOnClickListener(this);
        test_get_battery = (Button) rootView.findViewById(R.id.btn_test_battery);
        test_get_battery.setOnClickListener(this);
        test_normal = (Button) rootView.findViewById(R.id.btn_test_normal_mode);
        test_normal.setOnClickListener(this);


        List<BleDevice> connected_devices = BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice device : connected_devices) {
            connected_device = device;
            //device_name.setText(device.getName());
            //device_mac.setText(device.getMac());
            break;
        }

    }

    //write characteristic
    public boolean writeCharacteristic(byte[] value) {
        final boolean[] result = {false};
        BleManager.getInstance().write(connected_device,
                BluetoothUUID.RING_SERVICE.toString(),
                BluetoothUUID.RING_WRITE_CHAR.toString(),
                value,
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(int i, int i1, byte[] bytes) {

                        Toast.makeText(getActivity(), "写入成功:" + HexUtil.formatHexString(bytes), Toast.LENGTH_SHORT).show();
                        result[0] = true;
                    }

                    @Override
                    public void onWriteFailure(BleException e) {
                        Toast.makeText(getActivity(), "写入失败", Toast.LENGTH_SHORT).show();
                        result[0] = false;
                    }
                });
        return result[0];
    }

    //read characteristic
    public byte[] readCharacteristic() {
        BleManager.getInstance().read(
                connected_device,
                BluetoothUUID.RING_SERVICE.toString(),
                BluetoothUUID.RING_READ_CHAR.toString(),
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        // 读特征值数据成功

                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        // 读特征值数据失败
                    }
                });
        return null;
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        if (connected_device != null && bleDevice != null && connected_device.getKey().equals(bleDevice.getKey())) {
            //pop();
            popTo(OpenBluetoothFragment.class, false);
        }
    }

    @Override
    public void onDestroy() {
        ObserverManager.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                pop();
                break;
            case R.id.btn_test_mode:
                test_set_lmd.setEnabled(true);
                test_set_time.setEnabled(true);
                test_highlevel_time.setEnabled(true);
                test_lowlevel_time.setEnabled(true);
                setButtonBgGreen(test_mode);
                break;
            case R.id.btn_test_park:
                break;
            case R.id.btn_test_lmd: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("设置灵敏度");
                builder.setSingleChoiceItems(R.array.lmd, 3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_time: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("设置检测时间");
                builder.setSingleChoiceItems(R.array.time, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_highlevel_time: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("高电平时间设置:(秒)");
                builder.setView(R.layout.editbox);
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_lowlevel_time: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("低电平时间设置:(秒)");
                builder.setView(R.layout.editbox);
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_battery:
                break;
            case R.id.btn_test_normal_mode:
                break;
        }
    }
    public void setButtonBgGreen(View view){
        view.setBackgroundColor(Color.GREEN);
    }
    public void setButtonBgRed(View view){
        view.setBackgroundColor(Color.RED);
    }
}
