package com.example.weatherreport.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.example.weatherreport.Data.LocationUtils;
import com.example.weatherreport.Data.NetUtil;
import com.example.weatherreport.Model.Today;
import com.example.weatherreport.Model.Weather;
import com.example.weatherreport.Model.city;
import com.example.weatherreport.Model.icon;
import com.example.weatherreport.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Start extends AppCompatActivity {
    String City="";
    String cityNameIng="";
    ListView listView;
    TextView quality;
    String latitude;
    String longitude;
    TextView textViewTime;
    TextView temperature;
    TextView cityName;
    TextView notice;
    TextView humidity;
    ImageButton schedule;
    ImageView weatherIcon;
    ImageView music;
    ArrayList<icon> icons;
    String todayWeather = "";
    ArrayList<Weather> list;
    ArrayList<Today> TodayList;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                /*Location location = LocationUtils.getInstance(Start.this).showLocation();
                if (location != null) {
                    String tude = "纬度：" + location.getLatitude() + " 经度：" + location.getLongitude();
                    latitude = String.valueOf(location.getLatitude()); // 纬度
                    longitude = String.valueOf(location.getLongitude()); // 经度
                    // 尝试获取地址信息，并检查是否为null
                    address = LocationUtils.getAddress(Start.this, location);
                    if (address != null) {
                        Log.i("FLY.LocationUtils", "地址：" + address);
                    } else {
                        Log.i("FLY.LocationUtils", "无法获取地址信息");
                    }
                } else {
                    Log.i("FLY.LocationUtils", "无法获取位置信息");
                }

                 */

                ArrayList<String> strList = (ArrayList<String>) msg.obj;
//                ArrayAdapter adapter = new ArrayAdapter(Start.this, android.R.layout.simple_list_item_1, strList);
                MyAdapter myAdapter = new MyAdapter();
                icons = (ArrayList<icon>) JSONArray.parseArray(load(), icon.class);
                int resource = 0;
                for (icon icon : icons) {
                    if (icon.getWeather().equals(todayWeather)) {
                        resource = getResId(icon.getLocation(), R.mipmap.class);
                        Log.i("weatherIcon", String.valueOf(resource));
                        break;
                    }
                }
                weatherIcon.setImageResource(resource);
                cityName.setText(cityNameIng);
                listView.setAdapter(myAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Intent intent = new Intent(Start.this, WeatherinfoActivity.class);
                        intent.putExtra("msg", strList.get(position));
                        intent.putExtra("Location", position);
                        intent.putExtra("city", City);
                        intent.putExtra("cityName", cityNameIng);
                        Log.i("msg", strList.get(position));
                        startActivity(intent);
                    }
                });
                myAdapter.notifyDataSetChanged();
            } else if (msg.what == 2) {
                textViewTime.setText(new Date().toLocaleString());
            } else if (msg.what == 3) {
                for (Today today1 : TodayList) {
                    temperature.setText(today1.getWendu() + "℃");
                    notice.setText(today1.getGanmao());
                    humidity.setText("空气湿度： " + today1.getShidu());
                    quality.setText("空气质量: " + today1.getQuality());
                }
            }
        }

    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermission();
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());
        setContentView(R.layout.activity_start);
        textViewTime = findViewById(R.id.textView);
        listView = findViewById(R.id.listview);
        temperature = findViewById(R.id.temperature);
        notice = findViewById(R.id.notice);
        humidity = findViewById(R.id.humidity);
        quality = findViewById(R.id.quality);
        schedule = findViewById(R.id.schedule);
        cityName = findViewById(R.id.CityName);
        music = findViewById(R.id.Music);
        weatherIcon = findViewById(R.id.weatherIcon);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this, ScheduleActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(Start.this).toBundle());
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra("code")) {
            City = intent.getStringExtra("code");
            cityNameIng = intent.getStringExtra("cityName");
        }
        MyThread myThread = new MyThread();
        myThread.start();
        Thread thread = new Thread(new TimeThread());
        thread.start();
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this, CitymanageActivity.class);
                intent.putExtra("searchName", "");
                intent.putExtra("city", City);
                intent.putExtra("cityName", cityNameIng);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Start.this).toBundle());
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this, MP3.class);
                intent.putExtra("city", City);
                intent.putExtra("cityName", cityNameIng);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(Start.this).toBundle());
            }
        });
    }

    class TimeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (City.isEmpty()) {
                try {
                    cityNameIng = doInBackground();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                ArrayList<city> cities = new ArrayList<>();
                cities = (ArrayList<city>) JSONArray.parseArray(loadCity(), city.class);
                for (city city : cities) {
                    if (city.getCityName().equals(cityNameIng)) {
                        City = city.getCode();
                    }
                }
            }
                String url = "http://t.weather.itboy.net/api/weather/city/" + City;
                try {
                    String str = NetUtil.net(url, null, "GET");
                    Log.i("NET", str);
                    if (str.length() <= 300) {
                        Log.i("Error", "天气信息暂时无法获取");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Start.this, "该城市天气信息暂时无法获取", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cityNameIng = doInBackground();
                        ArrayList<city> cities = new ArrayList<>();
                        cities = (ArrayList<city>) JSONArray.parseArray(loadCity(), city.class);
                        for (city city : cities) {
                            if (city.getCityName().equals(cityNameIng)) {
                                City = city.getCode();
                            }
                        }
                        url = "http://t.weather.itboy.net/api/weather/city/" + City;
                        str = NetUtil.net(url, null, "GET");
                    }
                    int startIndex = str.indexOf("[");
                    int endIndex = str.indexOf("]");
                    String subStr = str.substring(startIndex, (endIndex + 1));
                    list = (ArrayList<Weather>) JSONArray.parseArray(subStr, Weather.class);
                    ArrayList<String> strList = new ArrayList<>();
                    int startToday = str.indexOf("\"data\":");
                    int endToday = str.indexOf("[");
                    String temp = str.substring(startToday, endToday);
                    int a = temp.indexOf("{");
                    int b = temp.indexOf(",\"forecast\":");
                    String today = temp.substring(a, b);
                    Log.i("today", today);
                    TodayList = (ArrayList<Today>) JSONArray.parseArray(today + "}", Today.class);
                    int flag = 0;
                    for (Weather weather : list) {
                        if (flag == 0) {
                            flag++;
                            todayWeather = weather.getType();
                            strList.add("    " + weather.getDate() + "日   " + weather.getType() + "\n    " + weather.getHigh() + "  " + weather.getLow() + "\n");
                        } else {
                            Log.i("Weather", weather.toString());
                            strList.add("    " + weather.getDate() + "日   " + weather.getType() + "\n    " + weather.getHigh() + "  " + weather.getLow() + "\n");
                        }
                    }
                    Message message = new Message();
                    message.what = 1;
                    message.obj = strList;
                    Message message1 = new Message();
                    message1.what = 3;
                    message1.obj = TodayList;
                    Message message2 = new Message();
                    message2.obj = todayWeather;
                    handler.sendMessage(message);
                    handler.sendMessage(message1);
                    handler.sendMessage(message2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private String load() {
        String content = null;
        File file = new File(getFilesDir(), "weatherIcon.txt");
        try {
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

    int getResId(String variableName, Class<?> c) {
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
            View view = View.inflate(Start.this, R.layout.item, null);
            Log.i("todayWeather", todayWeather);
            TextView textViewDate = view.findViewById(R.id.Date);
            TextView textViewHigh = view.findViewById(R.id.high);
            TextView textViewLow = view.findViewById(R.id.low);
            ImageView WeatherType = view.findViewById(R.id.WeatherType);
            Weather weather = list.get(position);
            textViewDate.setText(weather.getDate() + "日");
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

    protected String doInBackground() throws Exception {
        Location location = LocationUtils.getInstance(Start.this).showLocation();
        if (location != null) {
            String tude = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.d("FLY.LocationUtils", tude);
            latitude = String.valueOf(location.getLatitude());//纬度
            longitude = String.valueOf(location.getLongitude());//经度
        } else {
            return "北京";
        }
        String city ="";
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        int flag = 1;
        flag = sharedPreferences.getInt("locationMethod",1);
        Log.i("locationMethod", String.valueOf(flag));
        switch (flag) {
            case 0: {
                String url1 = "http://api.map.baidu.com/geocoder?output=json&location=" + latitude + "," + longitude + "&ak=ctUhHi7R6jHCdfo1rTOUKCOM654YwHXz";
                String str1 = NetUtil.net(url1, null, "GET");
                // 使用JSON解析库来解析响应字符串，这里以org.json为例
                JSONObject jsonObject = new JSONObject(str1);
                if (jsonObject.has("result")) {
                    JSONObject dataObject = jsonObject.getJSONObject("result");
                    if (dataObject.has("addressComponent")) {
                        JSONObject add = dataObject.getJSONObject("addressComponent");
                        if (add.has("city")) {
                            city = add.getString("city");
                        }
                    }
                }
            }
            case 1: {
                try {
                    String url2 = "https://api.kertennet.com/geography/locationInfo?lng=" + longitude + "&lat=" + latitude + "&appCode=jO31535K373VN3a59M1cJ1o9g3913baL";
                    String str2 = NetUtil.net(url2, null, "GET");
                    // 使用JSON解析库来解析响应字符串，这里以org.json为例
                    JSONObject jsonObject2 = new JSONObject(str2);
                    if (jsonObject2.has("data")) {
                        JSONObject dataObject2 = jsonObject2.getJSONObject("data");
                        if (dataObject2.has("city")) {
                            city = dataObject2.getString("city");
                        }
                    }
                } catch (JSONException e) {
                    // 处理JSON解析异常
                    e.printStackTrace();
                }
            }
        }
       /* Log.i("respond",str);
        int startIndex = str.indexOf("\"city\":\"");
        int endIndex = str.indexOf(
                "市\",");
        String subStr = str.substring(startIndex, (endIndex + 1));
        String city = subStr.substring(8,subStr.length());

        */
        int a = city.indexOf("市");
        city = city.substring(0,a);

        return city;
    }

    private boolean checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> lackedPermission = new ArrayList<>();
        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Log.i("Error","cannot get permission");
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        return true;

/*
    Geocoder geocoder;    //此对象能通过经纬度来获取相应的城市等信息

    void getCNByLocation(Context c) {

        geocoder = new Geocoder(c);
        //用于获取Location对象，以及其他
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager) c.getSystemService(serviceName);
        //provider的类型
        String provider = LocationManager.NETWORK_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    //高精度
        criteria.setAltitudeRequired(false);    //不要求海拔
        criteria.setBearingRequired(false);    //不要求方位
        criteria.setCostAllowed(false);    //不允许有话费
        criteria.setPowerRequirement(Criteria.POWER_LOW);    //低功耗

        //通过最后一次的地理位置来获得Location对象
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        String queryed_name = updateWithNewLocation(location);
        if ((queryed_name != null) && (0 != queryed_name.length())) {

            cityNameIng = queryed_name;
        }

        /*
         * 第二个参数表示更新的周期，单位为毫秒；第三个参数的含义表示最小距离间隔，单位是米
         * 设定每30秒进行一次自动定位
         */
    /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 30000, 50,
                locationListener);
        //移除监听器，在只有一个widget的时候，这个还是适用的
        locationManager.removeUpdates(locationListener);
    }

    /**
     * 方位改变时触发，进行调用
     */
    /*
    LocationListener locationListener = new LocationListener() {
        String tempCityName;

        public void onLocationChanged(Location location) {

            tempCityName = updateWithNewLocation(location);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityNameIng = tempCityName;
            }
        }

        public void onProviderDisabled(String provider) {
            tempCityName = updateWithNewLocation(null);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityNameIng = tempCityName;
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新location
     *
     * @param location
     * @return cityName
     */
    /*
    String updateWithNewLocation(Location location) {
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("无法获取地理信息");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
            }
        }
        if (mcityName.length() != 0) {

            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName;
        }
    }

    /**
     * 通过经纬度获取地址信息的另一种方法
     *
     * @param latitude
     * @param longitude
     * @return 城市名
     */
    /*
    public static String GetAddr(String latitude, String longitude) {
        String addr = "";

        /*
         * 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
         * 密钥可以随便写一个key=abc
         * output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
         */
    /*
        String url = String.format(
                "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
                latitude, longitude);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {

            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {

            httpsConn = (URLConnection) myURL.openConnection();

            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(
                        httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                if ((data = br.readLine()) != null) {
                    String[] retList = data.split(",");
                    if (retList.length > 2 && ("200".equals(retList[0]))) {
                        addr = retList[2];
                    } else {
                        addr = "";
                    }
                }
                insr.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return addr;
    }
    */
    }
    private String loadCity() {
        String content = null;
        File file = new File(getFilesDir(), "cityInfo.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[{\n" +
                    "\"cityName\":\"北京\",\n" +
                    "\"code\":\"101010100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"朝阳\",\n" +
                    "\"code\":\"101010300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"顺义\",\n" +
                    "\"code\":\"101010400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"怀柔\",\n" +
                    "\"code\":\"101010500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"通州\",\n" +
                    "\"code\":\"101010600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昌平\",\n" +
                    "\"code\":\"101010700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"延庆\",\n" +
                    "\"code\":\"101010800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"丰台\",\n" +
                    "\"code\":\"101010900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"石景山\",\n" +
                    "\"code\":\"101011000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大兴\",\n" +
                    "\"code\":\"101011100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"房山\",\n" +
                    "\"code\":\"101011200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"密云\",\n" +
                    "\"code\":\"101011300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"门头沟\",\n" +
                    "\"code\":\"101011400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"平谷\",\n" +
                    "\"code\":\"101011500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"八达岭\",\n" +
                    "\"code\":\"101011600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"佛爷顶\",\n" +
                    "\"code\":\"101011700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"汤河口\",\n" +
                    "\"code\":\"101011800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"密云上甸子\",\n" +
                    "\"code\":\"101011900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"斋堂\",\n" +
                    "\"code\":\"101012000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"霞云岭\",\n" +
                    "\"code\":\"101012100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"北京城区\",\n" +
                    "\"code\":\"101012200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海淀\",\n" +
                    "\"code\":\"101010200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"天津\",\n" +
                    "\"code\":\"101030100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宝坻\",\n" +
                    "\"code\":\"101030300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"东丽\",\n" +
                    "\"code\":\"101030400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"西青\",\n" +
                    "\"code\":\"101030500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"北辰\",\n" +
                    "\"code\":\"101030600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"蓟县\",\n" +
                    "\"code\":\"101031400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"汉沽\",\n" +
                    "\"code\":\"101030800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"静海\",\n" +
                    "\"code\":\"101030900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"津南\",\n" +
                    "\"code\":\"101031000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"塘沽\",\n" +
                    "\"code\":\"101031100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大港\",\n" +
                    "\"code\":\"101031200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"武清\",\n" +
                    "\"code\":\"101030200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宁河\",\n" +
                    "\"code\":\"101030700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"上海\",\n" +
                    "\"code\":\"101020100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宝山\",\n" +
                    "\"code\":\"101020300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"嘉定\",\n" +
                    "\"code\":\"101020500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南汇\",\n" +
                    "\"code\":\"101020600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"浦东\",\n" +
                    "\"code\":\"101021300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"青浦\",\n" +
                    "\"code\":\"101020800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"松江\",\n" +
                    "\"code\":\"101020900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"奉贤\",\n" +
                    "\"code\":\"101021000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"崇明\",\n" +
                    "\"code\":\"101021100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"徐家汇\",\n" +
                    "\"code\":\"101021200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"闵行\",\n" +
                    "\"code\":\"101020200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"金山\",\n" +
                    "\"code\":\"101020700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"石家庄\",\n" +
                    "\"code\":\"101090101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"张家口\",\n" +
                    "\"code\":\"101090301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"承德\",\n" +
                    "\"code\":\"101090402\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"唐山\",\n" +
                    "\"code\":\"101090501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"秦皇岛\",\n" +
                    "\"code\":\"101091101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"沧州\",\n" +
                    "\"code\":\"101090701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"衡水\",\n" +
                    "\"code\":\"101090801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"邢台\",\n" +
                    "\"code\":\"101090901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"邯郸\",\n" +
                    "\"code\":\"101091001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"保定\",\n" +
                    "\"code\":\"101090201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"廊坊\",\n" +
                    "\"code\":\"101090601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"郑州\",\n" +
                    "\"code\":\"101180101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"新乡\",\n" +
                    "\"code\":\"101180301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"许昌\",\n" +
                    "\"code\":\"101180401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"平顶山\",\n" +
                    "\"code\":\"101180501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"信阳\",\n" +
                    "\"code\":\"101180601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南阳\",\n" +
                    "\"code\":\"101180701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"开封\",\n" +
                    "\"code\":\"101180801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"洛阳\",\n" +
                    "\"code\":\"101180901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"商丘\",\n" +
                    "\"code\":\"101181001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"焦作\",\n" +
                    "\"code\":\"101181101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鹤壁\",\n" +
                    "\"code\":\"101181201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"濮阳\",\n" +
                    "\"code\":\"101181301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"周口\",\n" +
                    "\"code\":\"101181401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"漯河\",\n" +
                    "\"code\":\"101181501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"驻马店\",\n" +
                    "\"code\":\"101181601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"三门峡\",\n" +
                    "\"code\":\"101181701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"济源\",\n" +
                    "\"code\":\"101181801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"安阳\",\n" +
                    "\"code\":\"101180201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"合肥\",\n" +
                    "\"code\":\"101220101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"芜湖\",\n" +
                    "\"code\":\"101220301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"淮南\",\n" +
                    "\"code\":\"101220401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"马鞍山\",\n" +
                    "\"code\":\"101220501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"安庆\",\n" +
                    "\"code\":\"101220601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宿州\",\n" +
                    "\"code\":\"101220701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阜阳\",\n" +
                    "\"code\":\"101220801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"亳州\",\n" +
                    "\"code\":\"101220901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黄山\",\n" +
                    "\"code\":\"101221001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"滁州\",\n" +
                    "\"code\":\"101221101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"淮北\",\n" +
                    "\"code\":\"101221201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"铜陵\",\n" +
                    "\"code\":\"101221301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宣城\",\n" +
                    "\"code\":\"101221401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"六安\",\n" +
                    "\"code\":\"101221501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"巢湖\",\n" +
                    "\"code\":\"101221601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"池州\",\n" +
                    "\"code\":\"101221701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"蚌埠\",\n" +
                    "\"code\":\"101220201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"杭州\",\n" +
                    "\"code\":\"101210101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"舟山\",\n" +
                    "\"code\":\"101211101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"湖州\",\n" +
                    "\"code\":\"101210201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"嘉兴\",\n" +
                    "\"code\":\"101210301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"金华\",\n" +
                    "\"code\":\"101210901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"绍兴\",\n" +
                    "\"code\":\"101210501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"台州\",\n" +
                    "\"code\":\"101210601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"温州\",\n" +
                    "\"code\":\"101210701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"丽水\",\n" +
                    "\"code\":\"101210801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"衢州\",\n" +
                    "\"code\":\"101211001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宁波\",\n" +
                    "\"code\":\"101210401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"重庆\",\n" +
                    "\"code\":\"101040100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"合川\",\n" +
                    "\"code\":\"101040300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南川\",\n" +
                    "\"code\":\"101040400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"江津\",\n" +
                    "\"code\":\"101040500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"万盛\",\n" +
                    "\"code\":\"101040600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"渝北\",\n" +
                    "\"code\":\"101040700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"北碚\",\n" +
                    "\"code\":\"101040800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"巴南\",\n" +
                    "\"code\":\"101040900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"长寿\",\n" +
                    "\"code\":\"101041000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黔江\",\n" +
                    "\"code\":\"101041100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"万州天城\",\n" +
                    "\"code\":\"101041200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"万州龙宝\",\n" +
                    "\"code\":\"101041300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"涪陵\",\n" +
                    "\"code\":\"101041400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"开县\",\n" +
                    "\"code\":\"101041500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"城口\",\n" +
                    "\"code\":\"101041600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"云阳\",\n" +
                    "\"code\":\"101041700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"巫溪\",\n" +
                    "\"code\":\"101041800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"奉节\",\n" +
                    "\"code\":\"101041900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"巫山\",\n" +
                    "\"code\":\"101042000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"潼南\",\n" +
                    "\"code\":\"101042100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"垫江\",\n" +
                    "\"code\":\"101042200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"梁平\",\n" +
                    "\"code\":\"101042300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"忠县\",\n" +
                    "\"code\":\"101042400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"石柱\",\n" +
                    "\"code\":\"101042500\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大足\",\n" +
                    "\"code\":\"101042600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"荣昌\",\n" +
                    "\"code\":\"101042700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"铜梁\",\n" +
                    "\"code\":\"101042800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"璧山\",\n" +
                    "\"code\":\"101042900\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"丰都\",\n" +
                    "\"code\":\"101043000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"武隆\",\n" +
                    "\"code\":\"101043100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"彭水\",\n" +
                    "\"code\":\"101043200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"綦江\",\n" +
                    "\"code\":\"101043300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"酉阳\",\n" +
                    "\"code\":\"101043400\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"秀山\",\n" +
                    "\"code\":\"101043600\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"沙坪坝\",\n" +
                    "\"code\":\"101043700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"永川\",\n" +
                    "\"code\":\"101040200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"福州\",\n" +
                    "\"code\":\"101230101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"泉州\",\n" +
                    "\"code\":\"101230501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"漳州\",\n" +
                    "\"code\":\"101230601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"龙岩\",\n" +
                    "\"code\":\"101230701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"晋江\",\n" +
                    "\"code\":\"101230509\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南平\",\n" +
                    "\"code\":\"101230901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"厦门\",\n" +
                    "\"code\":\"101230201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宁德\",\n" +
                    "\"code\":\"101230301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"莆田\",\n" +
                    "\"code\":\"101230401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"三明\",\n" +
                    "\"code\":\"101230801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"兰州\",\n" +
                    "\"code\":\"101160101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"平凉\",\n" +
                    "\"code\":\"101160301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"庆阳\",\n" +
                    "\"code\":\"101160401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"武威\",\n" +
                    "\"code\":\"101160501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"金昌\",\n" +
                    "\"code\":\"101160601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"嘉峪关\",\n" +
                    "\"code\":\"101161401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"酒泉\",\n" +
                    "\"code\":\"101160801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"天水\",\n" +
                    "\"code\":\"101160901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"武都\",\n" +
                    "\"code\":\"101161001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临夏\",\n" +
                    "\"code\":\"101161101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"合作\",\n" +
                    "\"code\":\"101161201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"白银\",\n" +
                    "\"code\":\"101161301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"定西\",\n" +
                    "\"code\":\"101160201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"张掖\",\n" +
                    "\"code\":\"101160701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"广州\",\n" +
                    "\"code\":\"101280101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"惠州\",\n" +
                    "\"code\":\"101280301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"梅州\",\n" +
                    "\"code\":\"101280401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"汕头\",\n" +
                    "\"code\":\"101280501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"深圳\",\n" +
                    "\"code\":\"101280601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"珠海\",\n" +
                    "\"code\":\"101280701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"佛山\",\n" +
                    "\"code\":\"101280800\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"肇庆\",\n" +
                    "\"code\":\"101280901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"湛江\",\n" +
                    "\"code\":\"101281001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"江门\",\n" +
                    "\"code\":\"101281101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"河源\",\n" +
                    "\"code\":\"101281201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"清远\",\n" +
                    "\"code\":\"101281301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"云浮\",\n" +
                    "\"code\":\"101281401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"潮州\",\n" +
                    "\"code\":\"101281501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"东莞\",\n" +
                    "\"code\":\"101281601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"中山\",\n" +
                    "\"code\":\"101281701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阳江\",\n" +
                    "\"code\":\"101281801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"揭阳\",\n" +
                    "\"code\":\"101281901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"茂名\",\n" +
                    "\"code\":\"101282001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"汕尾\",\n" +
                    "\"code\":\"101282101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"韶关\",\n" +
                    "\"code\":\"101280201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南宁\",\n" +
                    "\"code\":\"101300101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"柳州\",\n" +
                    "\"code\":\"101300301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"来宾\",\n" +
                    "\"code\":\"101300401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"桂林\",\n" +
                    "\"code\":\"101300501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"梧州\",\n" +
                    "\"code\":\"101300601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"防城港\",\n" +
                    "\"code\":\"101301401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"贵港\",\n" +
                    "\"code\":\"101300801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"玉林\",\n" +
                    "\"code\":\"101300901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"百色\",\n" +
                    "\"code\":\"101301001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"钦州\",\n" +
                    "\"code\":\"101301101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"河池\",\n" +
                    "\"code\":\"101301201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"北海\",\n" +
                    "\"code\":\"101301301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"崇左\",\n" +
                    "\"code\":\"101300201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"贺州\",\n" +
                    "\"code\":\"101300701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"贵阳\",\n" +
                    "\"code\":\"101260101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"安顺\",\n" +
                    "\"code\":\"101260301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"都匀\",\n" +
                    "\"code\":\"101260401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"兴义\",\n" +
                    "\"code\":\"101260906\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"铜仁\",\n" +
                    "\"code\":\"101260601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"毕节\",\n" +
                    "\"code\":\"101260701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"六盘水\",\n" +
                    "\"code\":\"101260801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"遵义\",\n" +
                    "\"code\":\"101260201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"凯里\",\n" +
                    "\"code\":\"101260501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昆明\",\n" +
                    "\"code\":\"101290101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"红河\",\n" +
                    "\"code\":\"101290301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"文山\",\n" +
                    "\"code\":\"101290601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"玉溪\",\n" +
                    "\"code\":\"101290701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"楚雄\",\n" +
                    "\"code\":\"101290801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"普洱\",\n" +
                    "\"code\":\"101290901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昭通\",\n" +
                    "\"code\":\"101291001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临沧\",\n" +
                    "\"code\":\"101291101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"怒江\",\n" +
                    "\"code\":\"101291201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"香格里拉\",\n" +
                    "\"code\":\"101291301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"丽江\",\n" +
                    "\"code\":\"101291401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"德宏\",\n" +
                    "\"code\":\"101291501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"景洪\",\n" +
                    "\"code\":\"101291601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大理\",\n" +
                    "\"code\":\"101290201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"曲靖\",\n" +
                    "\"code\":\"101290401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"保山\",\n" +
                    "\"code\":\"101290501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"呼和浩特\",\n" +
                    "\"code\":\"101080101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"乌海\",\n" +
                    "\"code\":\"101080301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"集宁\",\n" +
                    "\"code\":\"101080401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"通辽\",\n" +
                    "\"code\":\"101080501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿拉善左旗\",\n" +
                    "\"code\":\"101081201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鄂尔多斯\",\n" +
                    "\"code\":\"101080701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临河\",\n" +
                    "\"code\":\"101080801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"锡林浩特\",\n" +
                    "\"code\":\"101080901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"呼伦贝尔\",\n" +
                    "\"code\":\"101081000\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"乌兰浩特\",\n" +
                    "\"code\":\"101081101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"包头\",\n" +
                    "\"code\":\"101080201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"赤峰\",\n" +
                    "\"code\":\"101080601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南昌\",\n" +
                    "\"code\":\"101240101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"上饶\",\n" +
                    "\"code\":\"101240301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"抚州\",\n" +
                    "\"code\":\"101240401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宜春\",\n" +
                    "\"code\":\"101240501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鹰潭\",\n" +
                    "\"code\":\"101241101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"赣州\",\n" +
                    "\"code\":\"101240701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"景德镇\",\n" +
                    "\"code\":\"101240801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"萍乡\",\n" +
                    "\"code\":\"101240901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"新余\",\n" +
                    "\"code\":\"101241001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"九江\",\n" +
                    "\"code\":\"101240201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吉安\",\n" +
                    "\"code\":\"101240601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"武汉\",\n" +
                    "\"code\":\"101200101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黄冈\",\n" +
                    "\"code\":\"101200501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"荆州\",\n" +
                    "\"code\":\"101200801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宜昌\",\n" +
                    "\"code\":\"101200901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"恩施\",\n" +
                    "\"code\":\"101201001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"十堰\",\n" +
                    "\"code\":\"101201101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"神农架\",\n" +
                    "\"code\":\"101201201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"随州\",\n" +
                    "\"code\":\"101201301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"荆门\",\n" +
                    "\"code\":\"101201401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"天门\",\n" +
                    "\"code\":\"101201501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"仙桃\",\n" +
                    "\"code\":\"101201601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"潜江\",\n" +
                    "\"code\":\"101201701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"襄樊\",\n" +
                    "\"code\":\"101200201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鄂州\",\n" +
                    "\"code\":\"101200301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"孝感\",\n" +
                    "\"code\":\"101200401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黄石\",\n" +
                    "\"code\":\"101200601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"咸宁\",\n" +
                    "\"code\":\"101200701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"成都\",\n" +
                    "\"code\":\"101270101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"自贡\",\n" +
                    "\"code\":\"101270301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"绵阳\",\n" +
                    "\"code\":\"101270401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南充\",\n" +
                    "\"code\":\"101270501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"达州\",\n" +
                    "\"code\":\"101270601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"遂宁\",\n" +
                    "\"code\":\"101270701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"广安\",\n" +
                    "\"code\":\"101270801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"巴中\",\n" +
                    "\"code\":\"101270901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"泸州\",\n" +
                    "\"code\":\"101271001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宜宾\",\n" +
                    "\"code\":\"101271101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"内江\",\n" +
                    "\"code\":\"101271201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"资阳\",\n" +
                    "\"code\":\"101271301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"乐山\",\n" +
                    "\"code\":\"101271401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"眉山\",\n" +
                    "\"code\":\"101271501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"凉山\",\n" +
                    "\"code\":\"101271601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"雅安\",\n" +
                    "\"code\":\"101271701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"甘孜\",\n" +
                    "\"code\":\"101271801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿坝\",\n" +
                    "\"code\":\"101271901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"德阳\",\n" +
                    "\"code\":\"101272001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"广元\",\n" +
                    "\"code\":\"101272101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"攀枝花\",\n" +
                    "\"code\":\"101270201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"银川\",\n" +
                    "\"code\":\"101170101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"中卫\",\n" +
                    "\"code\":\"101170501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"固原\",\n" +
                    "\"code\":\"101170401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"石嘴山\",\n" +
                    "\"code\":\"101170201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吴忠\",\n" +
                    "\"code\":\"101170301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"西宁\",\n" +
                    "\"code\":\"101150101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黄南\",\n" +
                    "\"code\":\"101150301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海北\",\n" +
                    "\"code\":\"101150801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"果洛\",\n" +
                    "\"code\":\"101150501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"玉树\",\n" +
                    "\"code\":\"101150601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海西\",\n" +
                    "\"code\":\"101150701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海东\",\n" +
                    "\"code\":\"101150201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海南\",\n" +
                    "\"code\":\"101150401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"济南\",\n" +
                    "\"code\":\"101120101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"潍坊\",\n" +
                    "\"code\":\"101120601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临沂\",\n" +
                    "\"code\":\"101120901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"菏泽\",\n" +
                    "\"code\":\"101121001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"滨州\",\n" +
                    "\"code\":\"101121101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"东营\",\n" +
                    "\"code\":\"101121201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"威海\",\n" +
                    "\"code\":\"101121301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"枣庄\",\n" +
                    "\"code\":\"101121401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"日照\",\n" +
                    "\"code\":\"101121501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"莱芜\",\n" +
                    "\"code\":\"101121601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"聊城\",\n" +
                    "\"code\":\"101121701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"青岛\",\n" +
                    "\"code\":\"101120201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"淄博\",\n" +
                    "\"code\":\"101120301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"德州\",\n" +
                    "\"code\":\"101120401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"烟台\",\n" +
                    "\"code\":\"101120501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"济宁\",\n" +
                    "\"code\":\"101120701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"泰安\",\n" +
                    "\"code\":\"101120801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"西安\",\n" +
                    "\"code\":\"101110101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"延安\",\n" +
                    "\"code\":\"101110300\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"榆林\",\n" +
                    "\"code\":\"101110401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"铜川\",\n" +
                    "\"code\":\"101111001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"商洛\",\n" +
                    "\"code\":\"101110601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"安康\",\n" +
                    "\"code\":\"101110701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"汉中\",\n" +
                    "\"code\":\"101110801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宝鸡\",\n" +
                    "\"code\":\"101110901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"咸阳\",\n" +
                    "\"code\":\"101110200\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"渭南\",\n" +
                    "\"code\":\"101110501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"太原\",\n" +
                    "\"code\":\"101100101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临汾\",\n" +
                    "\"code\":\"101100701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"运城\",\n" +
                    "\"code\":\"101100801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"朔州\",\n" +
                    "\"code\":\"101100901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"忻州\",\n" +
                    "\"code\":\"101101001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"长治\",\n" +
                    "\"code\":\"101100501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大同\",\n" +
                    "\"code\":\"101100201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阳泉\",\n" +
                    "\"code\":\"101100301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"晋中\",\n" +
                    "\"code\":\"101100401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"晋城\",\n" +
                    "\"code\":\"101100601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吕梁\",\n" +
                    "\"code\":\"101101100\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"乌鲁木齐\",\n" +
                    "\"code\":\"101130101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"石河子\",\n" +
                    "\"code\":\"101130301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昌吉\",\n" +
                    "\"code\":\"101130401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吐鲁番\",\n" +
                    "\"code\":\"101130501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"库尔勒\",\n" +
                    "\"code\":\"101130601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿拉尔\",\n" +
                    "\"code\":\"101130701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿克苏\",\n" +
                    "\"code\":\"101130801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"喀什\",\n" +
                    "\"code\":\"101130901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"伊宁\",\n" +
                    "\"code\":\"101131001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"塔城\",\n" +
                    "\"code\":\"101131101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"哈密\",\n" +
                    "\"code\":\"101131201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"和田\",\n" +
                    "\"code\":\"101131301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿勒泰\",\n" +
                    "\"code\":\"101131401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿图什\",\n" +
                    "\"code\":\"101131501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"博乐\",\n" +
                    "\"code\":\"101131601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"克拉玛依\",\n" +
                    "\"code\":\"101130201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"拉萨\",\n" +
                    "\"code\":\"101140101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"山南\",\n" +
                    "\"code\":\"101140301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阿里\",\n" +
                    "\"code\":\"101140701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昌都\",\n" +
                    "\"code\":\"101140501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"那曲\",\n" +
                    "\"code\":\"101140601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"日喀则\",\n" +
                    "\"code\":\"101140201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"林芝\",\n" +
                    "\"code\":\"101140401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"台北县\",\n" +
                    "\"code\":\"101340101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"高雄\",\n" +
                    "\"code\":\"101340201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"台中\",\n" +
                    "\"code\":\"101340401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"海口\",\n" +
                    "\"code\":\"101310101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"三亚\",\n" +
                    "\"code\":\"101310201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"东方\",\n" +
                    "\"code\":\"101310202\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"临高\",\n" +
                    "\"code\":\"101310203\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"澄迈\",\n" +
                    "\"code\":\"101310204\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"儋州\",\n" +
                    "\"code\":\"101310205\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"昌江\",\n" +
                    "\"code\":\"101310206\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"白沙\",\n" +
                    "\"code\":\"101310207\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"琼中\",\n" +
                    "\"code\":\"101310208\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"定安\",\n" +
                    "\"code\":\"101310209\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"屯昌\",\n" +
                    "\"code\":\"101310210\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"琼海\",\n" +
                    "\"code\":\"101310211\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"文昌\",\n" +
                    "\"code\":\"101310212\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"保亭\",\n" +
                    "\"code\":\"101310214\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"万宁\",\n" +
                    "\"code\":\"101310215\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"陵水\",\n" +
                    "\"code\":\"101310216\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"西沙\",\n" +
                    "\"code\":\"101310217\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南沙岛\",\n" +
                    "\"code\":\"101310220\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"乐东\",\n" +
                    "\"code\":\"101310221\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"五指山\",\n" +
                    "\"code\":\"101310222\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"琼山\",\n" +
                    "\"code\":\"101310102\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"长沙\",\n" +
                    "\"code\":\"101250101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"株洲\",\n" +
                    "\"code\":\"101250301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"衡阳\",\n" +
                    "\"code\":\"101250401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"郴州\",\n" +
                    "\"code\":\"101250501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"常德\",\n" +
                    "\"code\":\"101250601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"益阳\",\n" +
                    "\"code\":\"101250700\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"娄底\",\n" +
                    "\"code\":\"101250801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"邵阳\",\n" +
                    "\"code\":\"101250901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"岳阳\",\n" +
                    "\"code\":\"101251001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"张家界\",\n" +
                    "\"code\":\"101251101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"怀化\",\n" +
                    "\"code\":\"101251201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黔阳\",\n" +
                    "\"code\":\"101251301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"永州\",\n" +
                    "\"code\":\"101251401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吉首\",\n" +
                    "\"code\":\"101251501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"湘潭\",\n" +
                    "\"code\":\"101250201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南京\",\n" +
                    "\"code\":\"101190101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"镇江\",\n" +
                    "\"code\":\"101190301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"苏州\",\n" +
                    "\"code\":\"101190401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"南通\",\n" +
                    "\"code\":\"101190501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"扬州\",\n" +
                    "\"code\":\"101190601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"宿迁\",\n" +
                    "\"code\":\"101191301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"徐州\",\n" +
                    "\"code\":\"101190801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"淮安\",\n" +
                    "\"code\":\"101190901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"连云港\",\n" +
                    "\"code\":\"101191001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"常州\",\n" +
                    "\"code\":\"101191101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"泰州\",\n" +
                    "\"code\":\"101191201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"无锡\",\n" +
                    "\"code\":\"101190201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"盐城\",\n" +
                    "\"code\":\"101190701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"哈尔滨\",\n" +
                    "\"code\":\"101050101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"牡丹江\",\n" +
                    "\"code\":\"101050301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"佳木斯\",\n" +
                    "\"code\":\"101050401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"绥化\",\n" +
                    "\"code\":\"101050501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"黑河\",\n" +
                    "\"code\":\"101050601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"双鸭山\",\n" +
                    "\"code\":\"101051301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"伊春\",\n" +
                    "\"code\":\"101050801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大庆\",\n" +
                    "\"code\":\"101050901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"七台河\",\n" +
                    "\"code\":\"101051002\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鸡西\",\n" +
                    "\"code\":\"101051101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鹤岗\",\n" +
                    "\"code\":\"101051201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"齐齐哈尔\",\n" +
                    "\"code\":\"101050201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大兴安岭\",\n" +
                    "\"code\":\"101050701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"长春\",\n" +
                    "\"code\":\"101060101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"延吉\",\n" +
                    "\"code\":\"101060301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"四平\",\n" +
                    "\"code\":\"101060401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"白山\",\n" +
                    "\"code\":\"101060901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"白城\",\n" +
                    "\"code\":\"101060601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"辽源\",\n" +
                    "\"code\":\"101060701\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"松原\",\n" +
                    "\"code\":\"101060801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"吉林\",\n" +
                    "\"code\":\"101060201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"通化\",\n" +
                    "\"code\":\"101060501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"沈阳\",\n" +
                    "\"code\":\"101070101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"鞍山\",\n" +
                    "\"code\":\"101070301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"抚顺\",\n" +
                    "\"code\":\"101070401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"本溪\",\n" +
                    "\"code\":\"101070501\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"丹东\",\n" +
                    "\"code\":\"101070601\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"葫芦岛\",\n" +
                    "\"code\":\"101071401\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"营口\",\n" +
                    "\"code\":\"101070801\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"阜新\",\n" +
                    "\"code\":\"101070901\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"辽阳\",\n" +
                    "\"code\":\"101071001\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"铁岭\",\n" +
                    "\"code\":\"101071101\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"朝阳\",\n" +
                    "\"code\":\"101071201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"盘锦\",\n" +
                    "\"code\":\"101071301\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"大连\",\n" +
                    "\"code\":\"101070201\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"cityName\":\"锦州\",\n" +
                    "\"code\":\"101070701\"\n" +
                    "}\n" +
                    "]\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}