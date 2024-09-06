package com.example.weatherreport.Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

import java.util.Calendar;

public class ScheduleStart extends AppCompatActivity {

    private Button schedule;
    private Button feedback;
    private Button changeAccount;
    private TextView information;
    private ImageButton settingF;
    private Calendar calendar;

    String userName;
    Intent gIntent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences= getSharedPreferences("setting", Activity.MODE_PRIVATE);
        settingF = findViewById(R.id.setting);
        calendar = Calendar.getInstance();
        changeAccount = findViewById(R.id.changeAccount);
        information = findViewById(R.id.information);
        schedule = (Button)findViewById(R.id.calendar_button);
        feedback=(Button)findViewById(R.id.feedback_button);
        gIntent = getIntent();
        userName = gIntent.getStringExtra("userName");
        information.setText("欢迎，"+userName);
        settingF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleStart.this,setting.class);
                startActivity(intent);
            }
        });
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleStart.this,ScheduleActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", "");
                editor.putString("password", "");
                editor.commit();
                startActivity(intent);
            }
        });
        Intent intent  =new Intent(this, LongRunningService.class);
        startService(intent);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSchedule();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEmail();
            }
        });
    }
    private void SendEmail() {
        Intent intent=new Intent(this, EmailSender.class);
        startActivity(intent);
    }
    private void ShowSchedule() {
        Intent intent=new Intent(this, MyCalendar.class);
        startActivity(intent);
    }
}

