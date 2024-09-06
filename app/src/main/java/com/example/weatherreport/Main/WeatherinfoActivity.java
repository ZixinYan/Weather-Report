package com.example.weatherreport.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.example.weatherreport.Data.NetUtil;
import com.example.weatherreport.Model.Weather;
import com.example.weatherreport.Model.icon;
import com.example.weatherreport.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherinfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherinfo);
        textViewTime = findViewById(R.id.textView);
        cityName = findViewById(R.id.CityName);
        listView = findViewById(R.id.listview);
        temperature = findViewById(R.id.temperature);
        notice = findViewById(R.id.notice);
        humidity = findViewById(R.id.humidity);
        quality = findViewById(R.id.quality);
        weatherIcon = findViewById(R.id.weatherIcon);
        Date = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStart = new Intent(WeatherinfoActivity.this,Start.class);
                toStart.putExtra("code", City);
                toStart.putExtra("cityName", cityNameIng);
                startActivity(toStart);
            }
        });
        intent = getIntent();
        msg = intent.getStringExtra("msg");
        location = intent.getIntExtra("Location",0);
        City = intent.getStringExtra("city");
        cityNameIng = intent.getStringExtra("cityName");
        MyThread myThread = new MyThread();
        myThread.start();
    }
        String City;
        String cityNameIng;
        ListView listView;
        TextView quality;
        TextView textViewTime;
        TextView temperature;
        TextView notice;
        TextView humidity;
        TextView cityName;
        ImageView weatherIcon;
        ArrayList<icon> icons;
        ArrayList<Weather> list;
        TextView Date;
        Intent intent;
        String msg;
        Button button;
        int location;
        Weather Tweather = new Weather();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    ArrayList<String> strList = (ArrayList<String>) msg.obj;
                   // ArrayAdapter adapter = new ArrayAdapter(WeatherinfoActivity.this, android.R.layout.simple_list_item_1, strList);
                    MyAdapter myAdapter = new MyAdapter();
                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
                            Intent intent = new Intent(WeatherinfoActivity.this, WeatherinfoActivity.class);
                            intent.putExtra("msg",strList.get(position));
                            intent.putExtra("Location",position);
                            intent.putExtra("city",City);
                            intent.putExtra("cityName",cityNameIng);
                            Log.i("msg",strList.get(position));
                            startActivity(intent);
                        }
                    });
                    myAdapter.notifyDataSetChanged();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, location);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int choseDay = calendar.get(Calendar.DAY_OF_MONTH);
                Date.setText(year+"-"+(month+1)+"-"+choseDay);
                temperature.setText(Tweather.getLow()+"   "+Tweather.getHigh());
                humidity.setText(Tweather.getType());
                cityName.setText(cityNameIng);
                quality.setText(Tweather.getFx()+"   "+Tweather.getFl());
                notice.setText(Tweather.getNotice());
                icons = (ArrayList<icon>) JSONArray.parseArray(load(), icon.class);
                int resource = 0;
                for (icon icon: icons) {
                    if (icon.getWeather().equals(Tweather.getType())){
                        resource = getResId(icon.getLocation(),R.mipmap.class);
                        Log.i("weatherIcon", String.valueOf(resource));
                        break;
                    }
                }
                weatherIcon.setImageResource(resource);
            }

        };

        class MyThread extends Thread {
            @Override
            public void run() {
                super.run();
                String url = "http://t.weather.itboy.net/api/weather/city/" + City;
                try {
                    String str = NetUtil.net(url, null, "GET");
                    Log.i("NET", str);
                    int startIndex = str.indexOf("[");
                    int endIndex = str.indexOf("]");
                    String subStr = str.substring(startIndex, (endIndex + 1));
                    list = (ArrayList<Weather>) JSONArray.parseArray(subStr, Weather.class);
                    ArrayList<String> strList = new ArrayList<>();
                    int flag = 0;
                    for (Weather weather : list) {
                        if(flag==location){
                            Tweather = weather;
                            Log.i("TWeather", weather.toString());
                            strList.add("    " + weather.getDate() + "日   " + weather.getType() + "\n    " + weather.getHigh() + "  " + weather.getLow() + "\n");
                            flag++;
                        }else{
                            Log.i("Weather", weather.toString());
                            strList.add("    " + weather.getDate() + "日   " + weather.getType() + "\n    " + weather.getHigh() + "  " + weather.getLow() + "\n");
                            flag++;
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    message.obj = strList;
                    Message message2 = new Message();
                    message2.obj = Tweather;
                    handler.sendMessage(message);
                    handler.sendMessage(message2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String load() {
            String content = null;
            File file = new File(getFilesDir(), "weatherIcon.txt");
    /*try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[{\n" +
                    "  \"weather\":\"晴\",\n" +
                    "  \"location\":\"sunny\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"阴\",\n" +
                    "  \"location\":\"cloudy\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"雾\",\n" +
                    "  \"location\":\"haze\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"中雨\",\n" +
                    "  \"location\":\"drizzle\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"大雨\",\n" +
                    "  \"location\":\"drizzle\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"多云\",\n" +
                    "  \"location\":\"mostly_cloudy\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"小雨\",\n" +
                    "  \"location\":\"slight_drizzle\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"小雪\",\n" +
                    "  \"location\":\"drizzle_snow\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"中雪\",\n" +
                    "  \"location\":\"snow\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"大雪\",\n" +
                    "  \"location\":\"snow\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"雷阵雨\",\n" +
                    "  \"location\":\"thunderstorms_storms\"\n" +
                    "},\n" +
                    "{\n" +
                    "  \"weather\":\"thunderstorms_snow_storms\",\n" +
                    "  \"location\":\"thunderstorms_snow_storms\"\n" +
                    "}]");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

     */
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
            return content;
        }

        int getResId(String variableName, Class<?> c)
        {
            try {
                Field idField = c.getDeclaredField(variableName);
                return idField.getInt(idField);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(WeatherinfoActivity.this, R.layout.item, null);
            Log.i("todayWeather", Tweather.getType());
            TextView textViewDate = view.findViewById(R.id.Date);
            TextView textViewHigh = view.findViewById(R.id.high);
            TextView textViewLow = view.findViewById(R.id.low);
            ImageView WeatherType = view.findViewById(R.id.WeatherType);
            Weather weather = list.get(position);
            textViewDate.setText(weather.getDate()+"日");
            textViewHigh.setText(weather.getHigh());
            textViewLow.setText(weather.getLow());
            int resource = 0;
            for (icon icon : icons) {
                if (icon.getWeather().equals(weather.getType())) {
                    resource = getResId(icon.getLocation(), R.mipmap.class);
                    Log.i("weatherIcon", String.valueOf(resource));
                    break;
                }
            }
            WeatherType.setImageResource(resource);
            return view;
        }
    }

}