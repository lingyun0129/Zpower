package com.zpower.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.bluetooth.MyBluetoothManager;
import com.zpower.utils.BaseUtils;
import com.zpower.utils.MyLog;

public class UnitCalibrationActivity extends Activity {
    private EditText input;
    private Button btn_Send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_calibration);
        initView();
    }

    private void initView() {
        input=(EditText)findViewById(R.id.input);
        btn_Send=(Button)findViewById(R.id.btn_send);
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr=input.getText().toString();
                if(inputStr.isEmpty()){
                    Toast.makeText(UnitCalibrationActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                int inputNum=Integer.valueOf(inputStr);
                if (inputNum>65535){
                    Toast.makeText(UnitCalibrationActivity.this,"输入数字不能超过65535",Toast.LENGTH_SHORT).show();
                    input.setText("");
                    return;
                }
                //将inputNum转成字节数组
                byte[]buffer= BaseUtils.unsignedShortToByte2(inputNum);//高位在前，低位在后
                StringBuilder stringBuilder=new StringBuilder(2);
                stringBuilder.append(String.format("%2X ",buffer[0]));
                stringBuilder.append(String.format("%2X",buffer[1]));
                MyLog.e("cai",stringBuilder.toString());

                byte[] data=new byte[3];
                data[0]=0x0a;
                data[1]=buffer[1];//低位
                data[2]=buffer[0];//高位（看参数怎么传，高位在前还是低位在前）
                MyBluetoothManager.getInstance().writeCharacteristic(data);
            }
        });
    }
}
