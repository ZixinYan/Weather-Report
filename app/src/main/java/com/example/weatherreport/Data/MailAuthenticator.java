package com.example.weatherreport.Data;

import javax.mail.Authenticator;

public class MailAuthenticator extends Authenticator {
    String userName=null;
    String password=null;

    public MailAuthenticator(){
    }
    public MailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    // 这个方法在Authenticator内部会调用

    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(userName, password);
    }
}
