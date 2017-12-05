package com.zpower.ui.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.bluetooth.MyBluetoothManager;
import com.zpower.service.MainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by zx on 2017/2/25.
 */

public class DiscoveredFragment2 extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DiscoveredFragment";
    private ImageView iv_back;
    private View rootView;
    private ListView listView;
    private BluetoothDevice device;
    private ProgressDialog progressDialog;
    private ProgressBar scanPgr;
    private BLEDeviceListAdapter mAdater;
    public static DiscoveredFragment2 newInstance(){
        return new DiscoveredFragment2();
    }
    MyBluetoothManager myBluetoothManager = MyBluetoothManager.getInstance();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discovered2,container,false);
        initView();
        registerMyBTReceiver();
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initView() {
        listView=(ListView)rootView.findViewById(R.id.device_list);
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        mAdater=new BLEDeviceListAdapter();
        listView.setAdapter(mAdater);
        scanPgr=(ProgressBar)rootView.findViewById(R.id.scan_pgr);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                pop();
                break;
/*            case R.id.btn_bt_connect:
                int position=(Integer) v.getTag();
                final BluetoothDevice clickedDevice=mAdater.getDevice(position);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setMessage("正在连接设备:"+clickedDevice.getName());
                progressDialog.show();
                //在子线程中连接蓝牙设备
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MainService.getService().connectBLEDevice(clickedDevice);
                        }
                    }).start();
                break;*/
        }
    }


    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
//        super.onBluetoothConnect(device);
        Log.e(TAG,device.getAddress()+"连接成功");
        Toast.makeText(getActivity(),"连接成功！", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().postSticky(device);
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        start(ConnectedFragment.newInstance());
    }
    @Override
    public void onBluetoothDisconnect() {
        Toast.makeText(getActivity(),"连接失败请重试！", Toast.LENGTH_SHORT).show();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBluetoothConnecting() {
        Toast.makeText(getActivity(),"正在连接...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBluetoothDsiconnecting() {
//        super.onBluetoothDsiconnecting();
        Toast.makeText(getActivity(),"正在断开连接", Toast.LENGTH_LONG).show();

    }

    /***
     * 接收Eventbus消息
     * @param device
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventReceiver(BluetoothDevice device){
        this.device = device;
        if (mAdater!=null){
            mAdater.addDevice(device);
            mAdater.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // Adapter for holding devices found through scanning.
    private class BLEDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public BLEDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getActivity().getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.tv_bt_mac);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.tv_bt_name);
                viewHolder.btn_connect=(Button)view.findViewById(R.id.btn_bt_connect);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.btn_connect.setOnClickListener(new ListBtnListener(i));
            return view;
        }
    }
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        Button btn_connect;
    }
    public class ListBtnListener implements View.OnClickListener {

        int mPosition;

        public ListBtnListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            final BluetoothDevice clickedDevice=mAdater.getDevice(mPosition);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setMessage("Connecting:"+clickedDevice.getName());
            progressDialog.show();
            //在子线程中连接蓝牙设备
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MainService.getService().connectBLEDevice(clickedDevice);
                }
            }).start();
            myBluetoothManager.scanLeDevice(false);
        }

    }

    private void registerMyBTReceiver() {
        myBluetoothManager.registerBTReceiver(getActivity(), new MyBluetoothManager.OnRegisterBTReceiver() {

            /***
             * 发现新设备
             * @param device
             */
            @Override
            public void onBluetoothNewDevice(BluetoothDevice device) {
                if (device != null&&device.getName()!=null){
                    if (mAdater!=null){
                        mAdater.addDevice(device);
                        mAdater.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onBluetoothPairing(BluetoothDevice device) {

            }

            @Override
            public void onBluetoothPaired(BluetoothDevice device) {

            }

            @Override
            public void onBluetoothUnpaired(BluetoothDevice device) {

            }

            @Override
            public void onDiscoveryFinished() {
               scanPgr.setVisibility(View.INVISIBLE);
            }
        });
    }
}
