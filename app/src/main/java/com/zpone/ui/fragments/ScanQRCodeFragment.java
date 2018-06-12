package com.zpone.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zpone.R;

/**
 * Created by Administrator on 2017/12/3.
 */

public class ScanQRCodeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = ScanQRCodeFragment.class.getSimpleName();
    private ImageView iv_back;
    private View rootView;
    public static ScanQRCodeFragment newInstance() {return new ScanQRCodeFragment();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan_qr_code,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                pop();
                break;
        }
    }
}
