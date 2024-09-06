package com.example.weatherreport.Main;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.weatherreport.Data.CalendarReminderUtils;
import com.example.weatherreport.Model.MySchedule;
import com.example.weatherreport.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class LongRunningService extends Service {
    AlarmManager manager;
    ArrayList<MySchedule> list;
    CalendarReminderUtils calendarReminderUtils;
    public LongRunningService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        calendarReminderUtils=new CalendarReminderUtils(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "打印时间: " + new Date().
                        toString());
            }
        }).start();
        list= calendarReminderUtils.QueryCalendarEvent();
        Collections.sort(list, new Comparator<MySchedule>() {
            @Override
            public int compare(MySchedule o1, MySchedule o2) {
                return o1.getStart()-o2.getStart()>0?1:-1;
            }
        });
        Long nowTime=System.currentTimeMillis();
        for(int i=0;i<list.size();i++){
            MySchedule schedule=list.get(i);
            Long time=schedule.getStart();
            Long t=time-nowTime;
            if(t>=0&&t<86400000){
                SetAlarm(schedule,time);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @SuppressLint("ForegroundServiceType")
    private void ForegroundRun() {
        PendingIntent p_intent = PendingIntent.getActivity(this, 0,
                new Intent(this, MyCalendar.class), PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("ina-ina")
                .setContentTitle("MyCalendar")
                .setContentText("打开日程目录")
                .setContentIntent(p_intent)
                .setAutoCancel(true)
                .build();
        startForeground(0x1989, notification); //ID随便
    }
    private void SetAlarm(MySchedule schedule,Long time){
        Long startTime=time;
        String title=schedule.getTitle();
        String description=schedule.getDescription();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, StartService.class);
        intent.putExtra("id",schedule.getId());
        intent.putExtra("ScheduleId",schedule.getScheduleId());
        intent.putExtra("title",schedule.getTitle());
        intent.putExtra("description",schedule.getDescription());
        intent.putExtra("startTime",schedule.getStartTime());
        intent.putExtra("endTime",schedule.getEndTime());
        intent.putExtra("start",schedule.getStart());
        intent.putExtra("end", schedule.getEnd());
        PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        manager.set(AlarmManager.RTC_WAKEUP, startTime, pi);
    }
}
