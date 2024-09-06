package com.example.weatherreport.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"发送成功",Toast.LENGTH_SHORT).show();
        abortBroadcast();//是否允许广播继续发送
    }
}

