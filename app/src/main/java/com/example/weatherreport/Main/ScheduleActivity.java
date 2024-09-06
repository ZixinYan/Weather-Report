package com.example.weatherreport.Main;

import static com.example.weatherreport.Data.EncryptionUtils.hashPassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.Model.MySqlLite;
import com.example.weatherreport.Model.userFile;
import com.example.weatherreport.R;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    EditText userName;
    EditText passwords;
    Button logIn;
    Button register;
    String autoUserName;
    String autoPassword;
    CheckBox checkBox;
    ArrayList<userFile> userFiles = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new Slide());
        MySqlLite mySqlLite = new MySqlLite(this);
       // users = (ArrayList<user>) JSONArray.parseArray(load(), user.class);
        setContentView(R.layout.schedule);
        userName = findViewById(R.id.userName);
        passwords = findViewById(R.id.Password);
        logIn = findViewById(R.id.logIn);
        register = findViewById(R.id.register);
        checkBox = findViewById(R.id.checkBox);
        SharedPreferences sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        autoUserName = sharedPreferences.getString("userName", "");
        autoPassword = sharedPreferences.getString("password", "");
        userName.setText(autoUserName);
        passwords.setText(autoPassword);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String password = passwords.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(ScheduleActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(ScheduleActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else{
                    password = hashPassword(password);
                    boolean login = mySqlLite.login(username,password);
                    if(login){
                        Toast.makeText(ScheduleActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        if (checkBox.isChecked()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName", userName.getText().toString());
                            editor.putString("password", passwords.getText().toString());
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userName", "");
                            editor.putString("password", "");
                            editor.commit();
                        }
                        Intent intent= new Intent(ScheduleActivity.this,ScheduleStart.class);
                        intent.putExtra("userName", userName.getText().toString());
                        startActivity(intent);
                    }else{
                        Toast.makeText(ScheduleActivity.this, "账号密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                        passwords.setText("");
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, com.example.weatherreport.Main.register.class);
                startActivity(intent);
            }
        });
    }
   /* private String load() {
        String content = null;
        File file = new File(getFilesDir(), "user.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            bufferedReader.close();
            content = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("User",content);
        return content;
    }

    */
}