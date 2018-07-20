package com.zpone.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.zpone.R;
import com.zpone.model.BluetoothUUID;
import com.zpone.observer.Observer;
import com.zpone.observer.ObserverManager;
import com.zpone.utils.BaseUtils;
import com.zpone.utils.CMDUtils;

import java.util.List;

public class ConnectedDeviceInfoFragment extends BaseFragment implements Observer, View.OnClickListener {
    View rootView;
    TextView parking_status, network_status, battery_info;
    Button test_mode, test_park, test_set_lmd, test_set_time, test_highlevel_time, test_lowlevel_time, test_get_battery, test_normal;
    ImageView iv_back;
    BleDevice connected_device = null;
    byte[] cmd = new byte[3];//指令数组
    int selected_item = 2;//dialog siglechoice 选择项

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
        parking_status = (TextView) rootView.findViewById(R.id.device_park);
        network_status = (TextView) rootView.findViewById(R.id.device_network);
        battery_info = (TextView) rootView.findViewById(R.id.device_battery);

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
            break;
        }
        if (connected_device == null) {
            return;
        }
        BleManager.getInstance().notify(
                connected_device,
                BluetoothUUID.M306_SERVICE.toString(),
                BluetoothUUID.M306_NOTIFY_CHAR.toString(),
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        // 打开通知操作成功
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        // 打开通知操作失败
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        Log.e("cly", "接收到的数据:" + HexUtil.formatHexString(data));
                        Toast.makeText(getActivity(),"接收:"+ HexUtil.formatHexString(data),Toast.LENGTH_SHORT).show();
                        switch (data[0]) {
                            case 0x01:
                                test_park.setEnabled(true);
                                setButtonBgGreen(test_mode);
                                break;
                            case 0x02:
                                test_set_lmd.setEnabled(true);
                                setButtonBgGreen(test_park);
                                break;
                            case 0x03:
                                test_set_time.setEnabled(true);
                                setButtonBgGreen(test_set_lmd);
                                break;
                            case 0x04:
                                test_highlevel_time.setEnabled(true);
                                setButtonBgGreen(test_set_time);
                                break;
                            case 0x05:
                                test_lowlevel_time.setEnabled(true);
                                setButtonBgGreen(test_highlevel_time);
                                break;
                            case 0x06:
                                test_get_battery.setEnabled(true);
                                setButtonBgGreen(test_lowlevel_time);
                                break;
                            case 0x07:
                                test_normal.setEnabled(true);
                                setButtonBgGreen(test_get_battery);
                                break;
                            case 0x08:
                                setButtonBgGreen(test_normal);
                                break;
                        }
                    }
                });
    }

    //write characteristic
    public boolean writeCharacteristic(byte[] value) {
        final boolean[] result = {false};
        BleManager.getInstance().write(connected_device,
                BluetoothUUID.M306_SERVICE.toString(),
                BluetoothUUID.M306_WRITE_CHAR.toString(),
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
   /* public byte[] readCharacteristic() {
        BleManager.getInstance().read(
                connected_device,
                BluetoothUUID.M306_SERVICE.toString(),
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
    }*/

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
                //pop();
                popTo(OpenBluetoothFragment.class, false);
                break;
            case R.id.btn_test_mode:
                writeCharacteristic(CMDUtils.CMD_ENTER_TEST_MODE);
                break;
            case R.id.btn_test_park:
                writeCharacteristic(CMDUtils.CMD_PARK_TEST);
                break;
            case R.id.btn_test_lmd: {
                selected_item = 2;
                cmd[0] = CMDUtils.CMD_SET_LMD_TEST[0];
                cmd[1] = CMDUtils.CMD_SET_LMD_TEST[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("设置灵敏度");
                builder.setSingleChoiceItems(R.array.lmd, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_item = which;
                    }
                });
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cmd[2] = (byte) (selected_item + 1);
                        writeCharacteristic(cmd);
                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_time: {
                selected_item = 1;
                cmd[0] = CMDUtils.CMD_SET_TIME_TEST[0];
                cmd[1] = CMDUtils.CMD_SET_TIME_TEST[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("设置检测时间");
                builder.setSingleChoiceItems(R.array.time, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_item = which;

                    }
                });
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte time;
                        if (selected_item == 0) {
                            time = 0x02;//2秒
                        } else {
                            time = 0x0a;//10秒
                        }
                        cmd[2] = time;
                        writeCharacteristic(cmd);
                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_highlevel_time: {
                final EditText et_time = new EditText(getContext());
                et_time.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                et_time.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("高电平时间设置:(秒)");
                builder.setView(et_time);
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] cmd = new byte[4];
                        cmd[0] = CMDUtils.CMD_HIGHLEVEL_TIME_TEST[0];
                        cmd[1] = CMDUtils.CMD_HIGHLEVEL_TIME_TEST[1];
                        short time = Integer.valueOf(et_time.getText().toString()).shortValue();
                        byte[] time_bytes = BaseUtils.unsignedShortToByte2(time);
                        cmd[2] = time_bytes[0];
                        cmd[3] = time_bytes[1];
                        writeCharacteristic(cmd);
                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_lowlevel_time: {
                final EditText et_time = new EditText(getContext());
                et_time.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                et_time.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("低电平时间设置:(秒)");
                builder.setView(et_time);
                builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] cmd = new byte[4];
                        cmd[0] = CMDUtils.CMD_LOWLEVEL_TIME_TEST[0];
                        cmd[1] = CMDUtils.CMD_LOWLEVEL_TIME_TEST[1];
                        short time = Integer.valueOf(et_time.getText().toString()).shortValue();
                        byte[] time_bytes = BaseUtils.unsignedShortToByte2(time);
                        cmd[2] = time_bytes[0];
                        cmd[3] = time_bytes[1];
                        writeCharacteristic(cmd);
                    }
                });
                builder.show();
            }
            break;
            case R.id.btn_test_battery:
                writeCharacteristic(CMDUtils.CMD_BATTERY_TEST);
                break;
            case R.id.btn_test_normal_mode:
                writeCharacteristic(CMDUtils.CMD_ENTER_NORMAL_MODE);
                break;
        }
    }

    public void setButtonBgGreen(View view) {
        view.setBackgroundColor(Color.GREEN);
    }

    public void setButtonBgRed(View view) {
        view.setBackgroundColor(Color.RED);
    }
}
