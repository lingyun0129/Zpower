package com.vogtec.cr01.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vogtec.cr01.R;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingAccountFragment extends BaseFragment {
    public static SettingAccountFragment newInstance(){
        return new SettingAccountFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_account,container,false);
        return rootView;
    }
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
