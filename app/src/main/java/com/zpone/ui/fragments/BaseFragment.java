package com.zpone.ui.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpone.inter.BluetoothConnectCallback;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by guzhicheng on 2017/3/9.
 */

public class BaseFragment extends SupportFragment implements BluetoothConnectCallback{
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    @Override
    public void onBluetoothConnect(BluetoothDevice device) {

    }

    @Override
    public void onBluetoothDisconnect() {

    }

    @Override
    public void onBluetoothConnecting() {

    }

    @Override
    public void onBluetoothDsiconnecting() {

    }
}
