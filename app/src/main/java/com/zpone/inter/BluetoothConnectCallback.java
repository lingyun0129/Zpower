package com.zpone.inter;

import android.bluetooth.BluetoothDevice;

/**
 * Created by guzhicheng on 2017/3/9.
 * 蓝牙连接状态回调函数
 */

public interface BluetoothConnectCallback {

    public void onBluetoothConnect(BluetoothDevice device);

    public void onBluetoothDisconnect();

    public void onBluetoothConnecting();

    public void onBluetoothDsiconnecting();

}
