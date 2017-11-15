package com.zpower.service;

import android.os.Handler;
import android.os.Message;

import com.zpower.MessageTypes;
import com.zpower.model.DataRecord;
import com.zpower.utils.BaseUtils;
import com.zpower.utils.MyLog;
import com.zpower.view.FTMSConstant;

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
     * 接收indication 数据
     * @param buffer
     */
    public static void handlerIndicationData(byte[] buffer){
        //response data formate:responseCode+opCode+ResultCode+ResponseParameter(if present)
        if (buffer.length>=3){
            int responseCode=buffer[0];
            int opCode=buffer[1];
            int result=buffer[2];
            //校准
            if (buffer.length>=5&&opCode== FTMSConstant.OP_CALIBRATION){
                if (result==FTMSConstant.RESULT_SUCCESS) {
                    byte[] adc_default = new byte[2];
                    System.arraycopy(buffer, 3, adc_default, 0, 2);//把adc的值存入adc_default数组
                    sendDefaultADC(mHandler,BaseUtils.bytes2ToInt(adc_default,0));
                }
                else{
                    sendDefaultADC(mHandler,-1);//calibration failed
                    MyLog.e(TAG,"calibration failed !!! result="+result);
                }
            }
        }else{
            MyLog.e(TAG,"indication response Data is error.");
        }
    }
    /**
     * 接收蓝牙数据，并发送到UI
     *
     * @param buffer
     */
    public static void handlerBlueData(byte[] buffer){
        if(!mStartRead){
            return;
        }
        if(mHandler == null){
            return;
        }
        if (buffer.length == 8) {
            DataRecord d1 = new DataRecord(buffer);
/*            MyLog.e(TAG, "接收到的数据转Int后数据：" + BaseUtils.bytes2ToInt(d1.getFlag(), 0)
                    + " " + d1.getInsCadence() + " " + d1.getAvgCadence()
                    + " " + d1.getInsPower() + " " + d1.getAvgPower());*/
            sendBluetoothMessage(mHandler, d1);
        }else {
            MyLog.e(TAG,"Data is error.");
        }
    }

    /**
     * 接收剩余电量数据，发送到UI
     * @param level
     */
    public static void handleBatteryLevelData(int level){
        if(mHandler == null){
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = MessageTypes.MSG_BATTERY_LEVEL;
        msg.obj = level;
        msg.sendToTarget();
    }
    /**
     * 发送蓝牙数据消息
     * @param handler
     * @param data
     */
    private static void sendBluetoothMessage(Handler handler, DataRecord data){
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
    private static void sendDefaultADC(Handler handler, int data){
        Message msg = handler.obtainMessage();
        msg.what = MessageTypes.MSG_DEFAULT_ADC;
        msg.obj = data;
        msg.sendToTarget();
    }

}
