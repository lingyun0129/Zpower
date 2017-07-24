package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpower.R;

/**
 * Created by zx on 2017/3/6.
 */

public class MyDataFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private ImageView iv_switch_bg;
    private DataHistoryFragment dataHistoryFragment;
    private DataBestFragment dataBestFragment;

    public static MyDataFragment newInstance(){
        return new MyDataFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_data,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        TextView tv_best = (TextView) rootView.findViewById(R.id.tv_best);
        TextView tv_history = (TextView) rootView.findViewById(R.id.tv_history);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_switch_bg = (ImageView) rootView.findViewById(R.id.iv_switch_bg);
        iv_back.setOnClickListener(this);
        tv_best.setOnClickListener(this);
        tv_history.setOnClickListener(this);
        dataBestFragment = new DataBestFragment();
        dataHistoryFragment = new DataHistoryFragment();
        replaceLoadRootFragment(R.id.data_frameLayout, dataBestFragment,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
            case R.id.tv_best:
                iv_switch_bg.setImageDrawable(getResources().getDrawable(R.mipmap.best));
                replaceLoadRootFragment(R.id.data_frameLayout,dataBestFragment,true);
                break;
            case R.id.tv_history:
                iv_switch_bg.setImageDrawable(getResources().getDrawable(R.mipmap.history));
                replaceLoadRootFragment(R.id.data_frameLayout,dataHistoryFragment,true);
                break;
        }
    }
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
