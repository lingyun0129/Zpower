package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpower.R;
import com.zpower.utils.BaseUtils;
import com.zpower.utils.DBHelper;
import com.zpower.utils.SPUtils;

import java.math.BigDecimal;

/**
 * Created by zx on 2017/3/7.
 */

public class DataBestFragment extends BaseFragment {
    private TextView tvMaxWatt;
    private TextView tvLongestTime;
    private TextView tvMaxRpm;
    private TextView tvLongestDistance;
    private TextView tvMaxSpeed;
    private TextView tvMaxKcal;
    private View rootView;
    public static DataBestFragment newInstance() {
        return new DataBestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_best_data, container, false);
        initView();
        initData();
        return rootView;
    }
    private void initView() {
        tvMaxWatt = (TextView) rootView.findViewById(R.id.tv_maxWatt);
        tvLongestTime = (TextView) rootView.findViewById(R.id.tv_longestTime);
        tvMaxRpm = (TextView) rootView.findViewById(R.id.tv_maxRpm);
        tvLongestDistance = (TextView) rootView.findViewById(R.id.tv_longestDistance);
        tvMaxSpeed = (TextView) rootView.findViewById(R.id.tv_maxSpeed);
        tvMaxKcal = (TextView) rootView.findViewById(R.id.tv_maxKcal);
    }
    private void initData() {
        DBHelper dbHelper=new DBHelper(getActivity());
        tvMaxWatt.setText(SPUtils.get(getActivity(),"maxWatt",0)+"");
        tvMaxRpm.setText(SPUtils.get(getActivity(),"maxRpm",0)+"");
        float maxSpeed=(float)SPUtils.get(getActivity(),"maxSpeed",0.00f);
        BigDecimal bd = new BigDecimal(maxSpeed);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);//保留2位小数，四舍五入
        tvMaxSpeed.setText(bd+"");

        tvLongestDistance.setText(dbHelper.getMaxKm()+"");
        tvMaxKcal.setText(dbHelper.getMaxKcal()+"");

        tvLongestTime.setText(BaseUtils.coventLongTimeToStr(dbHelper.getLongestTime()));
    }
}
