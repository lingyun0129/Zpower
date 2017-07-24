package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpower.R;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingInfoFragment extends BaseFragment {
    public static SettingInfoFragment newInstance(){
        return new SettingInfoFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_info,container,false);
        initView();
        return rootView;
    }

    private void initView() {

    }
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
