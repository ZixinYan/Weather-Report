package com.example.weatherreport.Main;

import static com.example.weatherreport.Data.EncryptionUtils.hashPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.Model.MySqlLite;
import com.example.weatherreport.Model.User;
import com.example.weatherreport.R;

public class register extends AppCompatActivity {
    EditText userName;
    EditText password;
    Button register;
    User user = new User();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySqlLite mySqlLite = new MySqlLite(this);
        setContentView(R.layout.activity_register);
        userName = findViewById(R.id.userName2);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register2);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    Toast.makeText(com.example.weatherreport.Main.register.this,"Failure: user name or password cannot empty",Toast.LENGTH_SHORT).show();
                } else if (mySqlLite.checkRegister(userName.getText().toString())) {
                    Toast.makeText(com.example.weatherreport.Main.register.this,"Failure: user name have existed",Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", userName.getText().toString());
                    editor.putString("password", password.getText().toString());
                    user.username = userName.getText().toString();
                    user.password = hashPassword(password.getText().toString());
                    Log.i("Password", user.password);
                    Log.i("UserName", user.username);
                    editor.commit();
                    long u = mySqlLite.register(user);
                    finish();
                    //write(userName.getText().toString(),password.getText().toString());
                    if(u != -1) {
                        Toast.makeText(com.example.weatherreport.Main.register.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(com.example.weatherreport.Main.register.this, ScheduleActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(com.example.weatherreport.Main.register.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /*private void write(String userName,String password) {
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
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + ","+ "{\"userName\":\""+ userName + "\",\n" +
                    "\"password\":\""+password+"\"}");
            Log.i("register",content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
     */
}