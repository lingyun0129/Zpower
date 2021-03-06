package com.zpower.ui;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.inter.BluetoothConnectCallback;
import com.zpower.service.MainService;
import com.zpower.ui.fragments.BaseFragment;
import com.zpower.ui.fragments.LoginFragment;

import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity implements BluetoothConnectCallback{

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        MainService.getService().init(this,this);
        if (savedInstanceState == null){
            //replaceLoadRootFragment(R.id.main_frag, LoginFragment.newInstance(),false);
            loadRootFragment(R.id.main_frag, LoginFragment.newInstance());
            //loadRootFragment(R.id.main_frag, CyclingFragment.newInstance());
        }
    }
    @Override
    public void onBackPressedSupport() {

            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                    finish();
                } else {
                    TOUCH_TIME = System.currentTimeMillis();
                    Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT).show();
                }
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
        BaseFragment bf = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_frag);
        if(bf != null){
            bf.onBluetoothConnect(device);
        }
    }

    @Override
    public void onBluetoothDisconnect() {
        BaseFragment bf = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_frag);
        if(bf != null){
            bf.onBluetoothDisconnect();
        }
    }

    @Override
    public void onBluetoothConnecting() {
        BaseFragment bf = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_frag);
        if(bf != null){
            bf.onBluetoothConnecting();
        }
    }

    @Override
    public void onBluetoothDsiconnecting() {
        BaseFragment bf = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.main_frag);
        if(bf != null){
            bf.onBluetoothDsiconnecting();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "用户同意授权", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "用户拒绝授权", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
