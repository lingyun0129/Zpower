package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpower.R;
import com.zpower.utils.SPUtils;

/**
 * Created by zx on 2017/2/27.
 */

public class SettingAccountFragment extends BaseFragment implements View.OnClickListener{
    private View rootView;
    private EditText et_height,et_weight,et_age;
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
        TextView email=(TextView)rootView.findViewById(R.id.tv_id);
        email.setText(SPUtils.get(getActivity(),"user_email","").toString());
        et_height=(EditText)rootView.findViewById(R.id.et_height);
        et_weight=(EditText)rootView.findViewById(R.id.et_weight);
        et_age=(EditText)rootView.findViewById(R.id.et_age);
        String height=SPUtils.get(getActivity(),"user_height","").toString();
        String weight=SPUtils.get(getActivity(),"user_weight","").toString();
        String age=SPUtils.get(getActivity(),"user_age","").toString();
        if (!height.equals("")){
            et_height.setText(height);
        }
        if (!et_weight.equals("")){
            et_weight.setText(weight);
        }
        if (!et_age.equals("")){
            et_age.setText(age);
        }


    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        saveUserData();
        return true;
    }

    private void saveUserData() {
        if (!et_height.getText().toString().isEmpty()) {
            SPUtils.put(getActivity(),"user_height",et_height.getText().toString());
        }
        if (!et_weight.getText().toString().isEmpty()) {
            SPUtils.put(getActivity(),"user_weight",et_weight.getText().toString());
        }
        if (!et_age.getText().toString().isEmpty()) {
            SPUtils.put(getActivity(),"user_age",et_age.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                saveUserData();
                break;
        }

    }
}
