package com.zpower.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinelw.library.ColorArcProgressBar;
import com.zpower.R;
import com.zpower.inter.RecordDataCallback;
import com.zpower.service.MainService;
import com.zpower.utils.BaseUtils;

/**
 * Created by user on 2017/8/16.
 * Power by cly
 */

public class FTPTestFragment extends BaseFragment implements View.OnClickListener,RecordDataCallback {
    private View rootView;
    private TextView tv_countDownTime;
    private ColorArcProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private MainService mService;
    private final static long TRAINNING_TIME = 5 * 60 * 1000;

    public static FTPTestFragment newInstance() {
        return new FTPTestFragment();
    }
    public FTPTestFragment(){
        mService = MainService.getService();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ftp_tes, container, false);
        initView();
        initData();
        return rootView;
    }

    private void initData() {
        countDownTimer = new CountDownTimer(TRAINNING_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tv_countDownTime.setText(BaseUtils.coventLongTimeToStr(millisUntilFinished / 1000));
                //progressBar.setCurrentValues(millisUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                tv_countDownTime.setText("Done");
            }
        };
        countDownTimer.start();
        mService.startRecord(this);
    }

    private void initView() {
        tv_countDownTime = (TextView) rootView.findViewById(R.id.tv_count_down_time);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        progressBar = (ColorArcProgressBar) rootView.findViewById(R.id.progressbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                pop();
                countDownTimer.cancel();
                mService.stopRecord();
                break;
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        countDownTimer.cancel();
        mService.stopRecord();
        return true;
    }

    @Override
    public void onDataTotalHours(String totalTime) {

    }

    @Override
    public void onDataTotalKM(double totalKM) {

    }

    @Override
    public void onDataAvgWatt(int avgWATT) {

    }

    @Override
    public void onDataTotalCalores(double AVGWatt) {

    }

    @Override
    public void onDataMaxWatt(int maxWatt) {

    }

    @Override
    public void onDataWatt(int watt) {
        //progressBar.setCurrentValues(watt);
    }

    @Override
    public void onRPM(int rpm) {
        progressBar.setCurrentValues(rpm);
    }

    @Override
    public void onDefaultADC(int adc) {

    }

    @Override
    public void onDataMaxRpm(int rpm) {

    }

    @Override
    public void onDataMaxSpeed(float speed) {

    }
}
