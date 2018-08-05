package com.zpone.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zpone.MessageTypes;
import com.zpone.bluetooth.MyBluetoothManager;
import com.zpone.inter.BatteryLevelCallback;
import com.zpone.inter.BluetoothConnectCallback;
import com.zpone.inter.DefaultADCCallback;
import com.zpone.inter.RecordDataCallback;
import com.zpone.model.DataRecord;
import com.zpone.utils.BaseUtils;
import com.zpone.utils.MyLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guzhicheng on 2017/3/9.
 */

public class MainService {

    private final static String tag = MainService.class.getCanonicalName();
    private final double r = 0.17;
    private final double l =  2 * Math.PI * r;//曲柄周长
    private Context mContext;
    private static MainService mService;
    private BluetoothDevice mConnDevice;
    private boolean isInCycling = false;

    //重力加速度
    private final static float g = 9.8f;
    /**
     * 踏频
     */
    private int mFrquency = 0;
    int ADC = 0;//
    /**
     * 骑行参数相关回调函数
     */
    private RecordDataCallback mDataCallback;
    private BluetoothConnectCallback mBLEconnCallback;
    private DefaultADCCallback mDefaultADCCallback;
    private BatteryLevelCallback mBatteryLevelCallback;
    private MainService() {
    }

    public static MainService getService() {
        if (mService == null) {
            mService = new MainService();
        }
        return mService;
    }

    public void init(Context context, BluetoothConnectCallback callback) {
        mContext = context;
        mBLEconnCallback = callback;
    }
    private int LastRound = 0;
    private int round;
    private Handler mHandler = new Handler() {
        private int defaultADC;
        private double m;
        private double v1;
        private DataRecord preData=null;//上一条数据
        private DataRecord currentData=null;//当前数据
        private int p;
        private int count=0;
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessageTypes.MSG_CYCLE_TIMER:
/*                    if (LastRound == round){
                        mDataCallback.onRPM(0);
                        mDataCallback.onDataWatt(0);
                    }
                    LastRound = round;
                    Log.e(tag,"mFrquency"+mFrquency);*/
                    break;
                case MessageTypes.CONNECT_STATE_CONNECTED:
                    BluetoothDevice connectedDevice = (BluetoothDevice) msg.obj;
                    if (mBLEconnCallback == null || connectedDevice == null) {
                        return;
                    }
                    Log.e(tag, connectedDevice.getAddress() + "连接成功");
                    mConnDevice = connectedDevice;
                    mBLEconnCallback.onBluetoothConnect(connectedDevice);

                    break;
                case MessageTypes.CONNECT_STATE_DISCONNECTED:
                    if (mBLEconnCallback == null) {
                        return;
                    }
                    mBLEconnCallback.onBluetoothDisconnect();

                    break;
                case MessageTypes.CONNECT_STATE_CONNECTING:
                    if (mBLEconnCallback == null) {
                        return;
                    }
                    mConnDevice = null;
                    mBLEconnCallback.onBluetoothConnecting();

                    break;
                case MessageTypes.CONNECT_STATE_DISCONNECTING:
                    if (mBLEconnCallback == null) {
                        return;
                    }
                    mBLEconnCallback.onBluetoothDsiconnecting();

                    break;
                case MessageTypes.MSG_BLUETOOTH://蓝牙数据

                    //根据踏频计算相应参数
                    currentData=(DataRecord)msg.obj;
                    if(currentData==null||mDataCallback==null){
                        return;
                    }
                    if(preData==null&&currentData!=null){
                        preData=currentData;
                        return;
                    }
                    if(preData.getRounds()!=currentData.getRounds()) {
                        int InsCadence = getFrequency(preData.getRounds(), currentData.getRounds(), preData.getElapsedTime(), currentData.getElapsedTime());
                        if (InsCadence > 0) {
                            mFrquency = InsCadence;
                        }
                        mDataCallback.onRPM(mFrquency);//瞬时踏频
                        mDataCallback.onDataMaxRpm(mFrquency);//最大踏频
                        mDataCallback.onDataTotalKM(avgFrequency * l);//平均踏频*周长
                        if(mFrquency<60){
                            p=mFrquency/2;
                        }else if(mFrquency>=60||mFrquency<120)
                        {
                            p=(mFrquency*4)/5;
                        }else if (mFrquency>=120||mFrquency<200)
                        {
                            p=(mFrquency*3)/2;
                        }else if(mFrquency>=200){
                            p=mFrquency*2;
                        }
                        mDataCallback.onDataWatt(p);//当前功率
                        mDataCallback.onDataMaxWatt(p);//用来计算最大功率
                        mDataCallback.onDataAvgWatt(p);//用来计算平均功率
                        mDataCallback.onDataTotalCalores(p);//用来计算卡路里
                        preData = currentData;
                        count=0;
                    }
                    else{
                        count++;
                        if(count>3){
                            mDataCallback.onRPM(0);//瞬时踏频
                            mDataCallback.onDataMaxRpm(0);//最大踏频
                            //mDataCallback.onDataTotalKM(avgFrequency * l);//平均踏频*周长
                            mDataCallback.onDataWatt(0);//当前功率
                            mDataCallback.onDataMaxWatt(0);//用来计算最大功率
                            mDataCallback.onDataAvgWatt(0);//用来计算平均功率
                            //mDataCallback.onDataTotalCalores(p);//用来计算卡路里
                        }
                    }

                    /*DataModel dataModel = (DataModel) msg.obj;
                    if (dataModel == null) {
                        return;
                    }
                    if (mDataCallback == null) {
                        return;
                    }else{
                        ADC = getADC(dataModel.getAdc_data());//ADC的值
                        round = dataModel.getR_data();
                        int f = getFrequency(dataModel.getR_data(),dataModel.getSecond(),dataModel.getMilliSecond());//即时踏频
                        if(f >= 0){
                            mFrquency = f;
                        }
                        mDataCallback.onRPM(mFrquency);//瞬时踏频
                        mDataCallback.onDataMaxRpm(mFrquency);//最大踏频
                        mDataCallback.onDataTotalKM(avgFrequency*l);//平均踏频*周长
                        //曲柄速度
                        v1 = (mFrquency * l) / 60;
                        mDataCallback.onDataMaxSpeed((float) v1);//最大速度
                        //质量
                        m = (double)(Math.abs(ADC - defaultADC)) / 300000;
                        double F = m * g;//力
                        double p = F * v1;//功率
                        MyLog.e(tag,"ADC:功率"+ADC);
                        MyLog.e(tag, "ADC-力：" + F);

                        if (p < 450){
                            mDataCallback.onDataWatt((int) p);//当前功率
                            mDataCallback.onDataMaxWatt((int) p);//用来计算最大功率
                            mDataCallback.onDataAvgWatt((int) p);//用来计算平均功率
                            mDataCallback.onDataTotalCalores(p);//用来计算卡路里
                        }
                    }*/
                    break;
                case MessageTypes.MSG_DEFAULT_ADC:
                    defaultADC =(int)msg.obj; //getADC((byte[]) msg.obj);
                    mDefaultADCCallback.onDefaultADC(defaultADC);
                    Log.e(tag,"MainService defaultADC:"+ defaultADC +"");
                    break;
                case MessageTypes.MSG_BATTERY_LEVEL:
                    int battery_level=(int)msg.obj;
                    mBatteryLevelCallback.onBatterLevelDisplay(battery_level);
                    break;
            }
        }

    };

    private int avgFrequency = 0;//平均踏频
    private int frequency = 0;//瞬时踏频
    private boolean isFirstRound = true;
    private int firstTime;
    private int mTime;
    private int count = 0;
    private int frequencySum = 0;
    private int getFrequency(int r1,int r2,int t1,int t2){
        if (isFirstRound){
            isFirstRound = false;
        }else {
                int duration = t2-t1;
                if(duration!=0) {
                    frequency = (r2 - r1)*60*1024/duration;
                    Log.e(tag, "瞬时踏频:" + frequency);
                    count++;
                    frequencySum = frequencySum + frequency;
                    avgFrequency = frequencySum / count;
                    Log.e(tag, "平均踏频:" + avgFrequency);
                }
        }

        return  frequency;
    }
    private Timer mTimer = null;
    private TimerTask mTask = null;

    private void startTimer(){
        stopTimer();
        mTimer = new Timer("CycleTimer");
        mTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MessageTypes.MSG_CYCLE_TIMER);
            }
        };
        mTimer.schedule(mTask,0,4000);
    }

    private void stopTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTask.cancel();
            mTimer.purge();
            mTimer = null;
            mTask = null;
        }
    }

    private int getADC(byte[] data){
        int adc = 0;
        if(data == null || data.length != 3){
            return  0;
        }
        adc = BaseUtils.byte3ToInt(data,0);
        return adc;
    }

    /***
     *   功率计算
     *   P = F * v1;
     F = m * g
     m = (ADC – 初始值) /37106     初始值 int defaultValue

     说明
     P：功率    F:力    m：质量   g：重力加速度
     ADC为设备上传的数据 67438087

     速度计算
     l = 2 * pi * r;
     V1 = (f * l)/60
     V2 = V1 * M;
     说明
     V1：曲柄速度    V2:实际速度   f:踏频     l：曲柄周长    V：速度  M : 轮速比  r : 曲柄半径
        卡路里 = p*s/1000/4.18/0.22 (p:功率 s:时间)

     */

    /**
     * 开始骑行
     *
     * @param callback
     */
    public void startRecord(RecordDataCallback callback) {
        MyLog.e(tag, "startRecord");
        mDataCallback = callback;
        mFrquency = 0;
        setInCycling(true);
        BluetoothService.startReadingData(mHandler);
        startTimer();
    }

    /**
     * 停止读取数据
     */
    public void stopRecord() {
        //mMaxWatt = 0;
        mFrquency = 0;
        mDataCallback.onDataAvgWatt(0);//平均功率
        mDataCallback.onDataTotalCalores(0);
        mDataCallback.onRPM(0);
        mDataCallback.onDataTotalKM(0);
        BluetoothService.stopReadData();
        setInCycling(false);
        stopTimer();
    }

    /***
     * 暂停读取数据
     */
    public void pauseRecord(){
        BluetoothService.stopReadData();
        setInCycling(false);
        stopTimer();
    }
    public void getDefaultADC(DefaultADCCallback callback){
        BluetoothService.setHandler(mHandler);
        mDefaultADCCallback = callback;
    }

    public void setBatteryLevelCallback(BatteryLevelCallback callback){
        BluetoothService.setHandler(mHandler);
        mBatteryLevelCallback = callback;
    }
    public boolean isInCycling() {
        return isInCycling;
    }

    public void setInCycling(boolean cyc) {
        isInCycling = cyc;
    }

    /**
     * 连接设备
     *
     * @param device
     */
    public void connectBLEDevice(BluetoothDevice device) {
        MyBluetoothManager.getInstance().createBond(device, mHandler);

    }

}
