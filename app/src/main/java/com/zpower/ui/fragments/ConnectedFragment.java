package com.zpower.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpower.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zx on 2017/2/25.
 */

public class ConnectedFragment extends BaseFragment {
    private int time = 3;
    private TextView tv_count_down;
    Timer timer = new Timer();

    public static ConnectedFragment newInstance(){
        return new ConnectedFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connected,container,false);
        tv_count_down = (TextView) rootView.findViewById(R.id.tv_count_down);
        ImageView iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        timer.schedule(timerTask,1000,1000);
        return rootView;
    }
    Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case 1:
                    tv_count_down.setText(""+time);
                    if (time<0){
                        timer.cancel();
                        tv_count_down.setVisibility(View.GONE);
                        start(StartTrainingFragment.newInstance());
                    }
            }
        }
    };
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            time--;
            Message message = Message.obtain();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
}
