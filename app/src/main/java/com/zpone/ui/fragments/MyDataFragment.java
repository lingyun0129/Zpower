package com.zpone.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpone.R;

import static com.zpone.R.id.iv_back;

/**
 * Created by zx on 2017/3/6.
 */

public class MyDataFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private TextView tv_best,tv_history;
    private LinearLayout divider_line_best,divider_line_history;
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
        tv_best = (TextView) rootView.findViewById(R.id.tv_best);
        tv_history = (TextView) rootView.findViewById(R.id.tv_history);
        divider_line_best=(LinearLayout)rootView.findViewById(R.id.divider_line_best);
        divider_line_history=(LinearLayout)rootView.findViewById(R.id.divider_line_history);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_best.setOnClickListener(this);
        tv_history.setOnClickListener(this);
        divider_line_best.setOnClickListener(this);
        divider_line_history.setOnClickListener(this);
        dataBestFragment = new DataBestFragment();
        dataHistoryFragment = new DataHistoryFragment();
        replaceLoadRootFragment(R.id.data_frameLayout, dataBestFragment,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case iv_back:
                pop();
                break;
            case R.id.tv_best:
                tv_best.setTextColor(getResources().getColor(R.color.btn_bg));
                tv_history.setTextColor(getResources().getColor(R.color.white));
                divider_line_best.setVisibility(View.VISIBLE);
                divider_line_history.setVisibility(View.INVISIBLE);
                replaceLoadRootFragment(R.id.data_frameLayout,dataBestFragment,true);
                break;
            case R.id.tv_history:
                tv_best.setTextColor(getResources().getColor(R.color.white));
                tv_history.setTextColor(getResources().getColor(R.color.btn_bg));
                divider_line_best.setVisibility(View.INVISIBLE);
                divider_line_history.setVisibility(View.VISIBLE);
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
