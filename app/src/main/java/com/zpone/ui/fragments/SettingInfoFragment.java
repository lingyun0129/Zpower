package com.zpone.ui.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpone.R;
import com.zpone.bluetooth.MyBluetoothManager;
import com.zpone.inter.BatteryLevelCallback;
import com.zpone.service.MainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingInfoFragment extends BaseFragment implements View.OnClickListener ,BatteryLevelCallback{
    private View rootView;
    private TextView tv_bt_mac;
    private TextView tv_battery_level;
    public static SettingInfoFragment newInstance(){
        return new SettingInfoFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting_info,container,false);
        initView();
        getBatteryLevel();
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initView() {
        ImageView iv=(ImageView)rootView.findViewById(R.id.iv_back);
        iv.setOnClickListener(this);
        tv_bt_mac=(TextView)rootView.findViewById(R.id.tv_bt_mac);
        tv_battery_level=(TextView)rootView.findViewById(R.id.tv_battery_level);
        MainService.getService().setBatteryLevelCallback(this);
    }
    private void getBatteryLevel(){
        MyBluetoothManager.getInstance().readBatteryLevel();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
    /***
     * 接收Eventbus消息
     * @param device
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventReceiver(BluetoothDevice device){
        tv_bt_mac.setText(device.getAddress());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBatterLevelDisplay(int level) {
        if (level<10){
            Toast.makeText(getActivity(),R.string.low_battery,Toast.LENGTH_SHORT).show();
        }
        tv_battery_level.setText(level+"%");
    }
}
