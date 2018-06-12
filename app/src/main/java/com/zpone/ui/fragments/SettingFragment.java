package com.zpone.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zpone.R;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    public static SettingFragment newInstance(){
        return new SettingFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        RelativeLayout RL_my_account = (RelativeLayout) rootView.findViewById(R.id.RL_my_account);
        RelativeLayout RL_equipment_info = (RelativeLayout) rootView.findViewById(R.id.RL_equipment_info);
        RelativeLayout RL_mile_or_km = (RelativeLayout) rootView.findViewById(R.id.RL_mile_or_km);
        iv_back.setOnClickListener(this);
        RL_my_account.setOnClickListener(this);
        RL_equipment_info.setOnClickListener(this);
        RL_mile_or_km.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
            case R.id.RL_my_account:
                start(SettingAccountFragment.newInstance());
                break;
            case R.id.RL_mile_or_km:
                start(Mile_KMFragment.newInstance());
                break;
            case R.id.RL_equipment_info:
                start(SettingInfoFragment.newInstance());
                break;
        }

    }
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
