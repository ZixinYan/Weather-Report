package com.example.weatherreport.Model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MySqlLite extends SQLiteOpenHelper {

    //数据库名字
    private static final String DB_NAME = "User.db";
    //创建用户表
    private static final String CREATE_USER = "create table user(id integer primary key autoincrement," +
            "username varchar(30)," +
            "password varchar(30))";


    public MySqlLite(@Nullable Context context) {
        super(context,DB_NAME,null,1);
    }

    //创建数据表
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public long register(User u){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username ",u.getUsername());
        cv.put("password",u.getPassword());
        long users = db.insert("user",null,cv);
        return users;
    }
    //登录方法实现
    public boolean login(String username,String password){
        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;
        Cursor users = db.query("user", null, "username like?", new String[]{username}, null, null, null);
        if(users!=null){
            while (users.moveToNext()){
                @SuppressLint("Range") String username1 = users.getString(users.getColumnIndex("username"));
                Log.i("users", "login: "+username1);
                String password1 = users.getString(2);
                Log.i("users", "login: "+password1);
                result = password1.equals(password);
                return result;
            }
        }
        return false;
    }

    public boolean checkRegister(String username){
        SQLiteDatabase db = getWritableDatabase();
        Cursor users = db.query("user",null,"username like?",new String[]{username},null,null,null);
        if(users!=null){
            return true;
        }else{
            return false;
        }
    }

    //根据用户名查找当前登录用户信息
    public User select(String username){
        SQLiteDatabase db = getWritableDatabase();
        User SelectUser = new User();
        Cursor user = db.query("user", new String[]{"username"}, "username=?", new String[]{username}, null, null, null);
        while(user.moveToNext()){
            @SuppressLint("Range") String uname =user.getString(user.getColumnIndex("username"));
            SelectUser.setUsername(uname);
        }
        user.close();
        return SelectUser;
    }
}