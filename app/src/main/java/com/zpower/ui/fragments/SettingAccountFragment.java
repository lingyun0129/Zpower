package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zpower.R;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingAccountFragment extends BaseFragment implements View.OnClickListener{
    private View rootView;
    public static SettingAccountFragment newInstance(){
        return new SettingAccountFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting_account,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        ImageView iv=(ImageView)rootView.findViewById(R.id.iv_back);
        iv.setOnClickListener(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
        }

    }
}
