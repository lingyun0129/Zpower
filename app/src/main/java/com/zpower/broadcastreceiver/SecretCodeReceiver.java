package com.zpower.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zpower.ui.MagneticCalibrationActivity;

public class SecretCodeReceiver extends BroadcastReceiver {
    private static String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(SECRET_CODE_ACTION)){
                //Intent secretIntent=new Intent(context, UnitCalibrationActivity.class);
                Intent secretIntent=new Intent(context, MagneticCalibrationActivity.class);
                secretIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(secretIntent);
        }
    }
}
