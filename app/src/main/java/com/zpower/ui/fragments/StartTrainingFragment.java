package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.bluetooth.MyBluetoothManager;
import com.zpower.utils.MyLog;

/**
 * Created by zx on 2017/2/25.
 */

public class StartTrainingFragment extends BaseFragment {
    private static final String TAG = StartTrainingFragment.class.getCanonicalName();
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;
    public static StartTrainingFragment newInstance(){
        return new StartTrainingFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_training,container,false);
        Button btn_start_training = (Button) rootView.findViewById(R.id.btn_start_training);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        btn_start_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x00});
                if (status) {
                    start(CyclingRecordFragment.newInstance());
                } else {
                    MyLog.e("cly", "write 0x00 failed !");
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onBackPressedSupport() {

        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            MyBluetoothManager.getInstance().close();
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, "再按一次退出！", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG,"onBackPressedSupport 执行");
        return true;
    }
}
