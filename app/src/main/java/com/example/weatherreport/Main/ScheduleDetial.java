package com.example.weatherreport.Main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.Data.CalendarReminderUtils;
import com.example.weatherreport.Model.MySchedule;
import com.example.weatherreport.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ScheduleDetial extends AppCompatActivity {
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

    private String time;
    private String startTime,endTime;

    private MySchedule schedule;
    TextView textView;
    private EditText title;
    private EditText description;
    Button startButton;
    Button endButton;
    Button updateButton;
    Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detial);
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        View view = findViewById(R.id.activity_schedule_detial);
        view.setBackgroundColor(Color.parseColor(sharedPreferences.getString("color","#C8E0F3")));
        calendarUtils=new CalendarReminderUtils (this);
        calendar = Calendar.getInstance();
        textView=(TextView)findViewById(R.id.titleSetTitle) ;
        title= (EditText)findViewById(R.id.set_title);
        description=(EditText)findViewById(R.id.set_description);
        startButton=(Button)findViewById(R.id.start_time_button);
        endButton=(Button)findViewById(R.id.end_time_button);
        updateButton=(Button)findViewById(R.id.update_schedule_button);
        deleteButton=(Button)findViewById(R.id.delete_schedule_button);

        GetData();
        SetData();
    }

    private void SetData() {
        title.setText(schedule.getTitle());
        description.setText(schedule.getDescription());
        startButton.setText(schedule.getStartTime());
        endButton.setText(schedule.getEndTime());
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStartDate();
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetEndDate();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete();
            }
        });
    }

    private void Delete() {
        long id=Long.parseLong(schedule.getId());
        int flag=calendarUtils.DeleteSchedule(id);
        if(flag>0){
            Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
            finish();
        }

        else
            Toast.makeText(this,"删除失败",Toast.LENGTH_SHORT).show();
    }

    private void Update() {
        long start=calendar.getTimeInMillis(),end=calendar.getTimeInMillis()+10*60*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

        if(startTime==null)
            start= schedule.getStart();
        else{
            try {
                start = sdf.parse(startTime).getTime();//毫秒
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(endTime==null)
            end= schedule.getEnd();
        else{
            try {
                end=sdf.parse(endTime).getTime();//毫秒
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        long id=Long.parseLong(schedule.getId());
        String _title=title.getText().toString();
        String _description=description.getText().toString();
        int flag=calendarUtils.UpdateSchedule(id,_title,_description,start,end);
        if(flag>0){
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
            Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
    }

    private void GetData() {
        Intent intent = getIntent();
        schedule=new MySchedule();
        schedule.setId(intent.getStringExtra("id"));
        schedule.setScheduleId(intent.getStringExtra("ScheduleId"));
        schedule.setTitle(intent.getStringExtra("title"));
        schedule.setDescription(intent.getStringExtra("description"));
        schedule.setStartTime(intent.getStringExtra("startTime"));
        schedule.setEndTime(intent.getStringExtra("endTime"));
        schedule.setStart(intent.getLongExtra("start",0));
        schedule.setEnd(intent.getLongExtra("end",0));
    }
    private void GetStartTime(){
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
                time=strYear+"."+strMonth+"."+strDay+" "+strHour+":"+strMinute;
                startTime=strYear+strMonth+strDay+strHour+strMinute;
                startButton.setText(time);
            }
        }, hour, minute,true);

        timePickerDialog.show();


    }
    private void GetStartDate() {
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
                GetStartTime();

            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();


    }
    private void GetEndTime(){
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
                time=strYear+"."+strMonth+"."+strDay+" "+strHour+":"+strMinute;
                endTime=strYear+strMonth+strDay+strHour+strMinute;
                endButton.setText(time);
            }
        }, hour, minute,true);

        timePickerDialog.show();
    }
    private void GetEndDate() {
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
                GetEndTime();

            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
