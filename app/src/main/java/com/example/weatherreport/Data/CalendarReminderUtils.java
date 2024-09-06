package com.example.weatherreport.Data;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import com.example.weatherreport.Model.MySchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
public class CalendarReminderUtils {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "boohee";
    private static String CALENDARS_ACCOUNT_NAME = "xxxxx";
    private static String CALENDARS_ACCOUNT_TYPE = "xxxxx";
    private static String CALENDARS_DISPLAY_NAME = "xxxx";
    Context context;
    public CalendarReminderUtils (Context context) {
        this.context = context;
    }

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    @SuppressLint("Range")
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 添加日历事件
     */
    public int addCalendarEvent(Context context, String title, String description, long reminderTime) {
        if (context == null) {
            return -1;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return -1;
        }
        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        long currentTime=mCalendar.getTime().getTime();

        mCalendar.setTimeInMillis(reminderTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 10 * 60 * 1000);//设置终止时间，开始时间加10分钟
        long end = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        event.put("calendar_id", calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            return -1;
        }

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, 0);// 提前1分钟提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if(uri == null) { //添加事件提醒失败直接返回
            return -1;
        }
        return 1;
    }

    public ArrayList<MySchedule> QueryCalendarEvent(){
        Uri uri = Uri.parse(CALENDER_EVENT_URL);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<MySchedule> list=new ArrayList<MySchedule>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.moveToNext()) {

            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID));
            @SuppressLint("Range") String scheduleId = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
            @SuppressLint("Range") String start = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
            @SuppressLint("Range") String end = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND));
            long startTime=0;
            long endTime=0;
            if(start!=null)
                startTime=Long.parseLong(start);
            if(end!=null)
                endTime=Long.parseLong(end);


            MySchedule sc=new MySchedule();
            sc.setId(id);
            sc.setScheduleId(scheduleId);
            sc.setTitle(title);
            sc.setDescription(description);
            sc.setStart(startTime);
            sc.setEnd(endTime);
            sc.setStartTime(sdf.format(new Date(startTime)));
            sc.setEndTime(sdf.format(new Date(endTime)));
            list.add(sc);
        }
        return list;
    }
    public int UpdateSchedule(long id,String title,String description,Long startTime,Long endTime){

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;

        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION,description);
        values.put(CalendarContract.Events.DTSTART,startTime);
        values.put(CalendarContract.Events.DTEND,endTime);

        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
        int rows = context.getContentResolver().update(updateUri, values, null, null);
        return rows;

    }
    public int DeleteSchedule(Long id){
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
        return rows;
    }
}
