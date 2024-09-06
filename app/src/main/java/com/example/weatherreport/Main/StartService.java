package com.example.weatherreport.Main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.weatherreport.Data.CalendarReminderUtils;
import com.example.weatherreport.Model.MySchedule;
import com.example.weatherreport.R;

import java.util.ArrayList;

public class StartService extends Service {
    ArrayList<MySchedule> list;
    CalendarReminderUtils calendarReminderUtils;
    public StartService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        calendarReminderUtils=new CalendarReminderUtils(this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "收到定时广播", Toast.LENGTH_SHORT).show();
        MySchedule sc=new MySchedule();
        sc.setId(intent.getStringExtra("id"));
        sc.setScheduleId(intent.getStringExtra("scheduleId"));
        sc.setTitle(intent.getStringExtra("title"));
        sc.setDescription(intent.getStringExtra("description"));
        sc.setStart(intent.getLongExtra("start",0));
        sc.setEnd(intent.getLongExtra("end",0));
        sc.setStartTime(intent.getStringExtra("startTime"));
        sc.setEndTime(intent.getStringExtra("endTime"));

        ForegroundRun(sc);
        return super.onStartCommand(intent, flags, startId);
    }
    @SuppressLint("ForegroundServiceType")
    private void ForegroundRun(MySchedule schedule) {
        Intent intent= new Intent(this, ScheduleDetial.class);
        intent.putExtra("id",schedule.getId());
        intent.putExtra("ScheduleId",schedule.getScheduleId());
        intent.putExtra("title",schedule.getTitle());
        intent.putExtra("description",schedule.getDescription());
        intent.putExtra("startTime",schedule.getStartTime());
        intent.putExtra("endTime",schedule.getEndTime());
        intent.putExtra("start",schedule.getStart());
        intent.putExtra("end", schedule.getEnd());
        PendingIntent p_intent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("ina-ina")
                .setContentTitle(schedule.getTitle())
                .setContentText(schedule.getDescription())
                .setContentIntent(p_intent)
                .setAutoCancel(true)
                .build();
        startForeground(0x1989, notification); //ID随便
    }
}
