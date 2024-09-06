package com.example.weatherreport.Main;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.Data.CalendarReminderUtils;
import com.example.weatherreport.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddSchedule extends AppCompatActivity {
    private CalendarReminderUtils calendarUtils;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int year;       //年
    private int month;      //月
    private int day;        //日
    private int hour;       //时
    private int minute;     //分

    private String strYear;
    private String strMonth;
    private String strDay;
    private String strHour;
    private String strMinute;
    private View view;
    private String title;
    private String description;
    Button setDateButton;
    Button setTimeButton;
    Button addScheduleOkButtom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View view = findViewById(R.id.se);
        view.setBackgroundColor(Color.parseColor(sharedPreferences.getString("color","#C8E0F3")));
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);//获取当前年
        month = calendar.get(Calendar.MONTH)+1;//获取月份，加1是因为月份是从0开始计算的
        day = calendar.get(Calendar.DATE);//获取日
        hour = calendar.get(Calendar.HOUR);//获取小时
        minute = calendar.get(Calendar.MINUTE);//获取分钟

        calendarUtils=new CalendarReminderUtils (this);

        SetButton();
        Edit();
    }

    private void Edit() {
        final EditText titleText = (EditText) findViewById(R.id.title);
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title=titleText.getText().toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final EditText descriptionText = (EditText) findViewById(R.id.description);
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                description=descriptionText.getText().toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void SetButton() {
        setDateButton=(Button)findViewById(R.id.set_date_button);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDate();

            }
        });
        setTimeButton=(Button)findViewById(R.id.set_time_button);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTime();
            }
        });
        addScheduleOkButtom=(Button)findViewById(R.id.add_schedule_ok_button);
        addScheduleOkButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add();
            }
        });
    }

    private void Add() {

        String str=strYear+strMonth+strDay+strHour+strMinute;
        long millionSeconds=calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
        try {
            millionSeconds = sdf.parse(str).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int flag=calendarUtils.addCalendarEvent(this,title,description,millionSeconds);
        if(flag>0){
            Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show();
        }
    }

    private void GetTime(){
        timePickerDialog= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int _hour, int _minute) {
                hour=_hour;
                minute=_minute;
                strHour=String.valueOf(hour);
                strMinute=String.valueOf(minute);

                if(hour<10)
                    strHour="0"+strHour;
                if(minute<10)
                    strMinute="0"+strMinute;
                String str=strHour+":"+strMinute;
                setTimeButton.setText(str);
            }
        }, hour, minute,true);

        timePickerDialog.show();


    }
    private void GetDate() {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth) {
                year=_year;
                month=_monthOfYear+1;
                day=_dayOfMonth;
                strYear=String.valueOf(year);
                strMonth=String.valueOf(month);
                strDay=String.valueOf(day);
                if(month<10)
                    strMonth="0"+strMonth;
                if(day<10)
                    strDay="0"+strDay;

                String str=strYear+"."+strMonth+"."+strDay;
                setDateButton.setText(str);

            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();


    }
}
