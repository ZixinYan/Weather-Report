package com.example.weatherreport.Model;
//创建user类，并添加属性和构造方法、get、set方法
public class User {
    public String username;
    public String password;


    public User(){}
    public User(String username, String password, String phone, String emil) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}