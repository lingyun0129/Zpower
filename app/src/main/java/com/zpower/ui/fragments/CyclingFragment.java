package com.zpower.ui.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.zpower.R;
import com.zpower.bluetooth.MyBluetoothManager;
import com.zpower.inter.RecordDataCallback;
import com.zpower.model.RecordData;
import com.zpower.service.MainService;
import com.zpower.utils.DBHelper;
import com.zpower.utils.MyLog;
import com.zpower.utils.SPUtils;
import com.zpower.view.FTMSConstant;
import com.zpower.view.WaveLoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zxx on 2017/3/7.
 */

public class CyclingFragment extends BaseFragment implements View.OnClickListener,RecordDataCallback,OnChartGestureListener{
    private  final static String tag = CyclingFragment.class.getCanonicalName();
    private View rootView;
    private MainService mService;
    private TextView tv_device_name;
    private TextView tv_rpm;
    private TextView tv_max_watt;
    private TextView tv_watt;
    private ImageView iv_start_cycling;
    private boolean isCycling = true;
    private Chronometer chronometer;
    private TextView tv_connected;
    private TextView tv_avgWatt;
    private TextView tv_total_km;
    private long startTime;
    private long endTime;
    private TextView tv_kcal;
    private double totalTime;
    private ProgressDialog progressDialog;
    private BluetoothDevice mDevice;
    private WaveLoadingView waveLoadingView;
    private TextView tv_percent;
    private LineChart mChart;
    private RelativeLayout rl_restart;
    private RelativeLayout rl_stop;
    private ImageView iv_restart;
    private ImageView iv_stop;
    private boolean isConnected = false;
    private int maxWatt = 0;
    private int maxRpm=0;
    private float maxSpeed=0.0f;
    private float maxKcal=0.0f;
    private float longestDistance=0.0f;
    private long longestTime=0L;

    private RecordData recordData;
    public CyclingFragment(){
        mService = MainService.getService();
    }

    public static CyclingFragment newInstance(){
        return new CyclingFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(tag,"onCreateView执行");
        rootView = inflater.inflate(R.layout.fragment_cycling,container,false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        startCycling();
        startTime = System.currentTimeMillis();
        EventBus.getDefault().register(this);
        initChart();
        return rootView;
    }

    private void initChart() {
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        LineData data = new LineData();
        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        XAxis xl = mChart.getXAxis();
        xl.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setEnabled(false);
        leftAxis.setAxisMaximum((float)SPUtils.get(getActivity(),"ftp",FTMSConstant.FTP));
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        // modify the legend ...
        l.setForm(Legend.LegendForm.NONE);
    }
    private void addEntry(double watt) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), (float) watt), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");

        set.setDrawIcons(false);
        set.setColor(R.color.chartLineColor);
        set.setDrawValues(false);//不显示值
        set.setDrawCircles(false);//不显示圆点
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setLineWidth(1f);
        set.setDrawFilled(true);
        set.setDrawHighlightIndicators(false);//不显示点击后的交叉线
        set.setFormSize(15.f);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
            set.setFillDrawable(drawable);
        }
        else {
            set.setFillColor(R.color.chartDarkRed);
        }

        return set;
    }

    private void startCycling() {
        mService.startRecord(this);//开始读蓝牙数据
        iv_start_cycling.setImageDrawable(getResources().getDrawable(R.mipmap.play));
        //将时间设置为暂停时的时间
        chronometer.setBase(convertStrTimeToLong(chronometer.getText().toString()));
        chronometer.start();
        hideButtons();
        isCycling = true;
    }
    private void stopCycling(){

        mService.stopRecord();//停止读蓝牙数据
        iv_start_cycling.setImageDrawable(getResources().getDrawable(R.mipmap.pause));
        chronometer.stop();
        showButtons();
        isCycling = false;
    }
    private void pauseCycling(){
        mService.pauseRecord();
        iv_start_cycling.setImageDrawable(getResources().getDrawable(R.mipmap.pause));
        chronometer.stop();
        showButtons();
        isCycling = false;
        MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x08,0x02});//pause
    }
    private void restart() {
        MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x07});//start or resume
        mService.stopRecord();
        tv_avgWatt.setText("0");
        tv_max_watt.setText("0");
        tv_total_km.setText("0.00");
        tv_kcal.setText("0.00");
        chronometer.setText("00:00");
        startCycling();
    }

    private void initView() {
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_start_cycling = (ImageView) rootView.findViewById(R.id.start_cycling);
        tv_connected = (TextView) rootView.findViewById(R.id.tv_connected);
        tv_device_name = (TextView) rootView.findViewById(R.id.tv_device_name);
        tv_rpm = (TextView) rootView.findViewById(R.id.tv_rpm);//踏频-RPM
        tv_max_watt = (TextView) rootView.findViewById(R.id.tv_max_watt);
        tv_watt = (TextView) rootView.findViewById(R.id.tv_watt);
        tv_avgWatt = (TextView) rootView.findViewById(R.id.tv_AVGWatt);
        tv_total_km = (TextView) rootView.findViewById(R.id.tv_total_km);
        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        tv_kcal = (TextView) rootView.findViewById(R.id.tv_kcal);
        tv_percent = (TextView) rootView.findViewById(R.id.tv_percent);//百分比
        RelativeLayout RL_connected_bg = (RelativeLayout) rootView.findViewById(R.id.RL_connected_bg);
        waveLoadingView = (WaveLoadingView) rootView.findViewById(R.id.waveLoadingView);//水波球
        mChart = (LineChart) rootView.findViewById(R.id.lineChart);//折线图
        rl_restart = (RelativeLayout) rootView.findViewById(R.id.RL_restart);
        rl_stop = (RelativeLayout) rootView.findViewById(R.id.RL_stop);
        iv_restart = (ImageView) rootView.findViewById(R.id.iv_restart);
        iv_stop = (ImageView) rootView.findViewById(R.id.iv_stop);

        iv_back.setOnClickListener(this);
        iv_start_cycling.setOnClickListener(this);
        RL_connected_bg.setOnClickListener(this);
        mChart.setOnChartGestureListener(this);
        iv_restart.setOnClickListener(this);
        iv_stop.setOnClickListener(this);
    }
    private void showButtons(){
        rl_restart.setVisibility(View.VISIBLE);
        rl_stop.setVisibility(View.VISIBLE);
    }
    private void hideButtons(){
        rl_restart.setVisibility(View.INVISIBLE);
        rl_stop.setVisibility(View.INVISIBLE);
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.end_cycling);
        builder.setMessage(R.string.end_cycling_confim);
        builder.setNegativeButton(R.string.zpower_cancel,null);
        builder.setPositiveButton(R.string.zpower_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pop();
                saveRecordData();
            }
        });
        builder.show();
    }
    private void saveRecordData(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("MM月dd日");
        String date=dateFormat.format(new Date(startTime));
        MyLog.i("cly","current date="+date);
        String totalTime=chronometer.getText().toString();
        MyLog.i("cly","current totalTime="+totalTime);
        int avg_watt=Integer.parseInt(tv_avgWatt.getText().toString());
        MyLog.i("cly","current avg_watt="+avg_watt);
        int avg_rpm=Integer.parseInt(tv_rpm.getText().toString());
        MyLog.i("cly","current avg_rpm="+avg_rpm);
        double km=Double.parseDouble(tv_total_km.getText().toString());
        MyLog.i("cly","current km="+km);
        double calorie=Double.parseDouble(tv_kcal.getText().toString());
        MyLog.i("cly","current calorie="+calorie);
        recordData=new RecordData(date,totalTime,avg_watt,avg_rpm,km,calorie);

        DBHelper dbHelper=new DBHelper(getActivity());
        dbHelper.insertRecordData(recordData);
        EventBus.getDefault().postSticky(recordData);
        //stop
        if(!MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x08,0x01})){
            MyLog.e(tag,"write stop command failed");
        };
        //FileUtils.closeWriter();

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventReceiver(BluetoothDevice device){
        isConnected = true;
        mDevice = device;
        if (device != null){
            tv_connected.setVisibility(View.VISIBLE);
            tv_device_name.setText(device.getName());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                showDialog();
                break;
            case R.id.start_cycling:
                if (isCycling){
                    pauseCycling();
                }else {
                    startCycling();
                }
                break;
            case R.id.RL_connected_bg:
                if (!isConnected && mDevice != null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.setMessage(R.string.connecting_bluetooth + mDevice.getName());
                    progressDialog.show();
                    //在子线程中连接蓝牙设备
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mService.connectBLEDevice(mDevice);
                        }
                    }).start();
                }else if(mDevice == null){
                    start(OpenBluetoothFragment.newInstance());
                }
                break;
            case R.id.iv_restart:
                restart();
                break;
            case R.id.iv_stop:
                showDialog();
                break;
        }
    }

    /**
     * 将String类型的时间转换成long,如：12:01:08
     * @param strTime String类型的时间
     * @return long类型的时间
     * */
    protected long convertStrTimeToLong(String strTime) {
        // TODO Auto-generated method stub
        String []timeArry=strTime.split(":");
        long longTime=0;
        if (timeArry.length==2) {//如果时间是MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*1000*60+Integer.parseInt(timeArry[1])*1000;
        }else if (timeArry.length==3){//如果时间是HH:MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*1000*60*60+Integer.parseInt(timeArry[1])
                    *1000*60+Integer.parseInt(timeArry[0])*1000;
        }
        return SystemClock.elapsedRealtime()-longTime;
    }

    /***
     * 蓝牙数据回调
     */
    @Override
    public void onDataTotalHours(String totalTime) {


    }

    @Override
    public void onDataTotalKM(double totalKM) {
        //totalKM:每分钟行驶的米数
        totalTime = (double) (System.currentTimeMillis() - startTime)/(double) (1000*60);//共计多少分钟
        double km = (totalKM*totalTime*3.84/1000);//?为何要*3.84
        BigDecimal bd = new BigDecimal(km);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);//保留2位小数，四舍五入
        tv_total_km.setText(bd+"");

    }

    @Override
    public void onDataAvgWatt(int p) {
        MyLog.e(tag,"平均功率是："+p);
        tv_avgWatt.setText(p+"");
    }


    private double wattSum = 0;
    private int wattCount = 0;
    private  int getAVGWatt(int watt){
        wattCount++;
        wattSum = wattSum+watt;
        return (int) (wattSum/wattCount);
    }

    @Override
    public void onDataTotalCalores(double p) {
        double AVGWatt=p;
        BigDecimal bd = new BigDecimal(AVGWatt*totalTime*60/1000/4.184/0.22);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        tv_kcal.setText(bd+"");
        MyLog.e(tag,"当前消耗的卡路里："+bd);
    }

    /***
     * 最大功率
     * @param p
     */
    @Override
    public void onDataMaxWatt(int p) {
        if (p > maxWatt){
            maxWatt = p;
            MyLog.e(tag,"最大功率："+maxWatt);
            tv_max_watt.setText(maxWatt +"");
            SPUtils.put(getActivity(),"maxWatt",maxWatt);
        }
    }

    @Override
    public void onDataMaxRpm(int rpm) {
        if(maxRpm<rpm){
            maxRpm=rpm;
            MyLog.e(tag,"最大踏频："+maxRpm);
            SPUtils.put(getActivity(),"maxRpm",maxRpm);
        }
    }

    @Override
    public void onDataMaxSpeed(float speed) {
        if(maxSpeed<speed){
            maxSpeed=speed;
            MyLog.e(tag,"最大速度："+maxSpeed);
            SPUtils.put(getActivity(),"maxSpeed",maxSpeed);
        }
    }
    /***
     * 当前功率
     * @param watt
     */
    @Override
    public void onDataWatt(int watt) {
        MyLog.e(tag,"当前功率："+watt);
        tv_watt.setText(watt+"");
        int ftp=(int)SPUtils.get(getActivity(),"ftp",FTMSConstant.FTP);
        if (watt>ftp){
            watt = ftp;
        }
        addEntry(watt);
        int percent = watt*100/ftp;
        tv_percent.setText(percent+"%");
        waveLoadingView.setProgressValue((int) (watt/2.9));
    }

    /***
     * 踏频
     * @param rpm
     */
    @Override
    public void onRPM(int rpm) {
        tv_rpm.setText(rpm+"");
    }

    @Override
    public void onDefaultADC(int adc) {
        Log.e(tag,"CyclingFragment defaultADC:"+adc);
    }

    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
        MyLog.e(tag,"onBluetoothConnect"+device.getName());
        isConnected = true;
        if (tv_device_name.getVisibility() == View.GONE){
            tv_device_name.setVisibility(View.VISIBLE);
        }
        tv_connected.setVisibility(View.VISIBLE);
        tv_device_name.setText(device.getName());
        Toast.makeText(getActivity(), R.string.connect_success, Toast.LENGTH_SHORT).show();
        startCycling();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBluetoothDisconnect() {
        MyLog.e(tag,"onBluetoothDisconnect");
        isConnected = false;
        Toast.makeText(getActivity(), R.string.bt_disconnected, Toast.LENGTH_SHORT).show();
        tv_connected.setVisibility(View.GONE);
        tv_device_name.setText(R.string.connect_bluetooth);
        stopCycling();
    }
    @Override
    public boolean onBackPressedSupport() {
        showDialog();
        return true;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
