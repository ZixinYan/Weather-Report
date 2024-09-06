package com.example.weatherreport.Main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

public class setting extends AppCompatActivity {
    private Switch changeBackground;
    private Switch changeLocation;
    private View rootLayout;
    private SharedPreferences sharePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 获取根布局和 Switch 的引用
        rootLayout = findViewById(R.id.main);
        changeBackground = findViewById(R.id.changeBackground);
        changeLocation = findViewById(R.id.changeLocation);

        // 获取 SharedPreferences
        sharePreferences = getSharedPreferences("setting", MODE_PRIVATE);

        // 恢复 Switch 的状态
        boolean isChecked = sharePreferences.getBoolean("status", false);
        changeBackground.setChecked(isChecked);
        boolean isChecked2 = sharePreferences.getBoolean("status2", false);
        changeLocation.setChecked(isChecked2);

        // 根据 Switch 的状态设置背景颜色
        String color = sharePreferences.getString("color", "#C8E0F3"); // 默认颜色为浅蓝色
        rootLayout.setBackgroundColor(Color.parseColor(color));

        // 设置 Switch 的状态变化监听器
        changeBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharePreferences.edit();
                if (isChecked) {
                    rootLayout.setBackgroundColor(Color.parseColor("#335F81"));
                    editor.putString("color", "#335F81");
                    editor.putBoolean("status", true);
                } else {
                    rootLayout.setBackgroundColor(Color.parseColor("#C8E0F3"));
                    editor.putString("color", "#C8E0F3");
                    editor.putBoolean("status", false);
                }
                editor.apply();
            }
        });

        changeLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked2) {
                SharedPreferences.Editor editor = sharePreferences.edit();
                if (isChecked2) {
                    editor.putInt("locationMethod", 0);
                    editor.putBoolean("status2", true);
                } else {
                    editor.putInt("locationMethod",1);
                    editor.putBoolean("status2", false);
                }
                editor.apply();
            }
        });
    }
}
