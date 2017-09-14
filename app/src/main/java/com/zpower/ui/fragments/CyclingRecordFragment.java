package com.zpower.ui.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shinelw.library.ColorArcProgressBar;
import com.zpower.R;
import com.zpower.bluetooth.MyBluetoothManager;
import com.zpower.inter.DefaultADCCallback;
import com.zpower.model.RecordData;
import com.zpower.service.MainService;
import com.zpower.utils.SPUtils;
import com.zpower.view.FTMSConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zx on 2017/2/27.
 */

public class CyclingRecordFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = CyclingFragment.class.getCanonicalName();
    private View rootView;
    private TextView tv_device_name;
    private TextView tvTotalTime;
    private TextView tvTotalKilometre;
    //private TextView tvTotalWatt;
    private ColorArcProgressBar prg_avg_watt;
    private TextView tvTotalKcal;

    private BluetoothDevice device;
    private TextView tv_connected;
    private ProgressDialog progressDialog;
    private boolean isConnected = false;
    private RelativeLayout rl_connected_bg;
    private MainService mService;
    boolean isReset = false;

    public CyclingRecordFragment() {
        mService = MainService.getService();
    }

    public static CyclingRecordFragment newInstance() {
        return new CyclingRecordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cycling_record, container, false);
        initView();
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initView() {
        ImageView iv_setting = (ImageView) rootView.findViewById(R.id.iv_setting);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        ImageView iv_best_record = (ImageView) rootView.findViewById(R.id.iv_best_record);
        ImageView iv_start_cycling = (ImageView) rootView.findViewById(R.id.iv_start_cycling);
        ImageView iv_reset = (ImageView) rootView.findViewById(R.id.iv_reset);
        rl_connected_bg = (RelativeLayout) rootView.findViewById(R.id.RL_connected_bg);
        tv_device_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_connected = (TextView) rootView.findViewById(R.id.tv_connected);

        tvTotalTime = (TextView) rootView.findViewById(R.id.tv_total_time);
        tvTotalKilometre = (TextView) rootView.findViewById(R.id.tv_total_kilometre);
        prg_avg_watt = (ColorArcProgressBar) rootView.findViewById(R.id.prg_avg_watt);
        tvTotalKcal = (TextView) rootView.findViewById(R.id.tv_total_kcal);
        prg_avg_watt.setMaxValues((float)SPUtils.get(getActivity(),"ftp", FTMSConstant.FTP));
        iv_back.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_best_record.setOnClickListener(this);
        iv_start_cycling.setOnClickListener(this);
        rl_connected_bg.setOnClickListener(this);
        iv_reset.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting:
                start(SettingFragment.newInstance());
                break;
            case R.id.iv_best_record:
                start(MyDataFragment.newInstance());
                break;
            case R.id.iv_start_cycling:
                //isReset = true;
/*                if (isReset) {
                    start(CyclingFragment.newInstance(), STANDARD);
                } else {
                    Toast.makeText(getActivity(), "请先校准ADC", Toast.LENGTH_SHORT).show();
                }*/
                //发送控制指令--start or resume
                boolean success = MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x07});
                if (success){
                    start(CyclingFragment.newInstance(), STANDARD);
                }else{
                    Toast.makeText(getActivity(), "write 0x07 failed !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                pop();
                break;
            case R.id.RL_connected_bg:
                if (!isConnected && device != null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.setMessage(R.string.connecting_bluetooth + device.getName());
                    progressDialog.show();
                    //在子线程中连接蓝牙设备
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MainService.getService().connectBLEDevice(device);
                        }
                    }).start();
                } else if (device == null) {
                    start(OpenBluetoothFragment.newInstance());
                }
                break;
            case R.id.iv_reset:
                mService.getDefaultADC(defaultADCCallback);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.adc_calibration);
                builder.setMessage(R.string.calibration_hint);
                builder.setNegativeButton(R.string.zpower_cancel, null);
                builder.setPositiveButton(R.string.zpower_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x09});
                        if (success) {
                            isReset = true;
                            Log.e(TAG, "writeDataToCharacteristic(new byte[]{0x09}");
                        }
                    }
                });
                builder.show();

                break;
        }

    }

    DefaultADCCallback defaultADCCallback = new DefaultADCCallback() {
        @Override
        public void onDefaultADC(int adc) {
            int defaultADC = adc;
            if(defaultADC==-1){
                Toast.makeText(getActivity(), "ADC Clibration Failed ！", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "defaultADC:" + defaultADC, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventReceiver(BluetoothDevice device) {
        isConnected = true;
        this.device = device;
        if (this.device != null) {
            tv_connected.setVisibility(View.VISIBLE);
            tv_device_name.setText(device.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventReceiver(Boolean isSkipped) {
        if (isSkipped && !isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.bt_disconnect);
            builder.setMessage(R.string.bt_connect_now);
            builder.setNegativeButton(R.string.zpower_cancel, null);
            builder.setPositiveButton(R.string.zpower_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    start(OpenBluetoothFragment.newInstance());
                }
            });
            builder.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventReceiver(RecordData data) {
        tvTotalTime.setText(data.getTime());
        tvTotalKilometre.setText(data.getKm() + "");
        //tvTotalWatt.setText(data.getAvg_p() + "");
        prg_avg_watt.setCurrentValues(data.getAvg_p());
        tvTotalKcal.setText(data.getCalorie() + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
        isConnected = true;
        if (this.device != null) {
            tv_connected.setVisibility(View.VISIBLE);
            tv_device_name.setText(device.getName());
        }
        Toast.makeText(getActivity(), R.string.connected_title, Toast.LENGTH_SHORT).show();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBluetoothDisconnect() {
        isConnected = false;
        tv_connected.setVisibility(View.GONE);
        tv_device_name.setText(R.string.connect_bluetooth);
    }

}
