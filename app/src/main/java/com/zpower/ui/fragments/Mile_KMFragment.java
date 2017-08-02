package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zpower.R;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by zx on 2017/2/28.
 */

public class Mile_KMFragment extends BaseFragment implements View.OnClickListener {


    private View rootView;
    private ImageView iv_mile;
    private ImageView iv_km;
    public static Mile_KMFragment newInstance(){
        return new Mile_KMFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mile_or_km,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_mile = (ImageView) rootView.findViewById(R.id.iv_mile);
        iv_km = (ImageView) rootView.findViewById(R.id.iv_km);
        RelativeLayout RL_mile = (RelativeLayout) rootView.findViewById(R.id.RL_mile);
        RelativeLayout RL_km = (RelativeLayout) rootView.findViewById(R.id.RL_km);
        iv_back.setOnClickListener(this);
        RL_mile.setOnClickListener(this);
        RL_km.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
            case R.id.RL_mile:
                if (iv_mile.getVisibility() == View.INVISIBLE){

                    iv_mile.setVisibility(View.VISIBLE);
                }
                if (iv_km.getVisibility() == View.VISIBLE){

                    iv_km.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.RL_km:
                if (iv_km.getVisibility() == View.INVISIBLE){

                    iv_km.setVisibility(View.VISIBLE);
                }
                if (iv_mile.getVisibility() == View.VISIBLE){

                    iv_mile.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

}
