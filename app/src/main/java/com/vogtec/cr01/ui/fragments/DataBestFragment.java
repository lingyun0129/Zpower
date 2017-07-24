package com.vogtec.cr01.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vogtec.cr01.R;

/**
 * Created by zx on 2017/3/7.
 */

public class DataBestFragment extends BaseFragment {
    public static DataBestFragment newInstance(){
        return new DataBestFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_best_data,container,false);
        return rootView;
    }
}
