package com.zpower.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zpower.MessageTypes;
import com.zpower.model.DataModel;
import com.zpower.utils.MyLog;

import java.util.Arrays;

/**
 * Created by guzhicheng on 2017/3/7.
 * 处理数据接收和解析
 */

public class BluetoothService {

    public final static String TAG = BluetoothService.class.getCanonicalName();

    private  static boolean mStartRead = false;
    private  static Handler mHandler;
    /**
     * 开始等待读取蓝牙传过来的数据
     *
     * @param handler
     */
    public static void startReadingData(Handler handler){
        mStartRead = true;
        mHandler = handler;
    }

    /**
     * 停止读数据
     */
    public static void stopReadData(){
        mStartRead = false;
        mHandler = null;
    }

    /**
     * 接收蓝牙数据，并发送到UI
     *
     * @param buffer
     */
    public static void handlerBlueData(byte[] buffer){
        Log.e(TAG, "buffer:"+Arrays.toString(buffer));
        if(!mStartRead){
            return;
        }
        if(mHandler == null){
            return;
        }
        if (buffer.length == 7) {
            if (buffer[0] == -1){
                byte[] adc_default = new byte[3];//ADC初始值
                System.arraycopy(buffer, 3, adc_default, 0, 3);//把adc的值存入adc_default数组
                sendDefaultADC(mHandler,adc_default);
            }else {
                DataModel d1 =  new DataModel();
                d1.value(buffer);
                sendBluetoothMessage(mHandler,d1);
            }
        }else {
            MyLog.e(TAG,"Data is error.");
        }
    }

    /**
     * 发送蓝牙数据消息
     * @param handler
     * @param data
     */
    private static void sendBluetoothMessage(Handler handler, DataModel data){
        Message msg = handler.obtainMessage();
        msg.what = MessageTypes.MSG_BLUETOOTH;
        msg.obj = data;
        msg.sendToTarget();
    }

    /***
     * 发送默认ADC
     * @param handler
     * @param data
     */
    private static void sendDefaultADC(Handler handler, byte[] data){
        Message msg = handler.obtainMessage();
        msg.what = MessageTypes.MSG_DEFAULT_ADC;
        msg.obj = data;
        msg.sendToTarget();
    }

}
