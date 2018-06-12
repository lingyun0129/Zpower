package com.zpone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zpone.R;
import com.zpone.bluetooth.MyBluetoothManager;

public class MagneticCalibrationActivity extends Activity implements View.OnClickListener{
    private Button max_btn,min_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic_calibration);
        max_btn=(Button)findViewById(R.id.max_btn);
        max_btn.setOnClickListener(this);
        min_btn=(Button)findViewById(R.id.min_btn);
        min_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.max_btn:
                if (MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x13})){
                    Toast.makeText(this,"写入最大值成功",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.min_btn:
                if (MyBluetoothManager.getInstance().writeCharacteristic(new byte[]{0x12})){
                    Toast.makeText(this,"写入最小值成功",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
