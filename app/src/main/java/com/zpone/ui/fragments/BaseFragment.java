package com.zpone.ui.fragments;

import android.bluetooth.BluetoothDevice;

import com.zpone.inter.BluetoothConnectCallback;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by guzhicheng on 2017/3/9.
 */

public class BaseFragment extends SupportFragment implements BluetoothConnectCallback{

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
