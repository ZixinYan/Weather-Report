package com.example.weatherreport.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.weatherreport.Main.LongRunningService;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "收到定时广播", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, LongRunningService.class);
        context.startService(i);
    }
}
