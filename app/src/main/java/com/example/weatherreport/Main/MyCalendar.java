package com.example.weatherreport.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.Data.CalendarReminderUtils;
import com.example.weatherreport.Model.MySchedule;
import com.example.weatherreport.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyCalendar extends AppCompatActivity {
    private static final String TAG = "MyCalendar";
    CalendarReminderUtils calendarReminderUtils;
    ArrayList<MySchedule>list;
    ListView listView;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);
        calendarReminderUtils=new CalendarReminderUtils(this);
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        View view = findViewById(R.id.activity_my_calendar);
        view.setBackgroundColor(Color.parseColor(sharedPreferences.getString("color","#C8E0F3")));
        QueryMyschedule();
        SetData();
    }
    private void QueryMyschedule() {
        list= calendarReminderUtils.QueryCalendarEvent();
        Collections.sort(list, new Comparator<MySchedule>() {
            @Override
            public int compare(MySchedule o1, MySchedule o2) {
                return o1.getStart()-o2.getStart()>0?1:-1;
            }
        });
        ArrayAdapter<MySchedule> adapter=new ArrayAdapter<MySchedule>(MyCalendar.this,android.R.layout.simple_list_item_1,list);
        listView=(ListView)findViewById(R.id.schedule_listview);
        listView.setAdapter(adapter);
    }
    private void SetData() {
        Button addCalendarButton=(Button)findViewById(R.id.add_button);
        addCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyCalendar.this,AddSchedule.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MySchedule schedule=list.get(position);
                Intent intent = new Intent(MyCalendar.this, ScheduleDetial.class);
                intent.putExtra("id",schedule.getId());
                intent.putExtra("ScheduleId",schedule.getScheduleId());
                intent.putExtra("title",schedule.getTitle());
                intent.putExtra("description",schedule.getDescription());
                intent.putExtra("startTime",schedule.getStartTime());
                intent.putExtra("endTime",schedule.getEndTime());
                intent.putExtra("start",schedule.getStart());
                intent.putExtra("end", schedule.getEnd());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        QueryMyschedule();
    }
}

