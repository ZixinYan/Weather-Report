package com.example.weatherreport.Data;

import com.example.weatherreport.Model.MailSenderInfo;

public class EmailUtil {
        private static final String TAG = "EmailUtil";
        private static String SMTP_Password="gxqiemxottqsbgbe";//
        private static String SMTP_Host="smtp.qq.com";
        private static String Sender="2094548571@qq.com";


        public static boolean SendTextMail(String title,String msuggestions,String toAddress) {
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setMailServerHost(SMTP_Host);//smtp地址
            mailInfo.setMailServerPort("25");
            mailInfo.setValidate(true);
            mailInfo.setUserName(Sender);// 发送方邮件地址
            mailInfo.setPassword(SMTP_Password);// 邮箱POP3/SMTP服务授权码
            mailInfo.setFromAddress(Sender);// 发送方邮件地址
            mailInfo.setToAddress(toAddress);//接受方邮件地址
            mailInfo.setSubject(title);//设置邮箱标题
            mailInfo.setContent(msuggestions);
            // 这个自定义的类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            return sms.sendTextMail(mailInfo);// 发送文体格式
        }


        public static boolean SendTextAndFileMail(String title,String msuggestions,String toAddress,String[] filePath,int pathIndex) {
            MailSenderInfo mailInfo = new MailSenderInfo();
            mailInfo.setMailServerHost(SMTP_Host);//smtp地址
            mailInfo.setMailServerPort("25");
            mailInfo.setValidate(true);
            mailInfo.setUserName(Sender);// 发送方邮件地址
            mailInfo.setPassword(SMTP_Password);// 邮箱POP3/SMTP服务授权码
            mailInfo.setFromAddress(Sender);// 发送方邮件地址
            mailInfo.setToAddress(toAddress);//接受方邮件地址
            mailInfo.setSubject(title);//设置邮箱标题
            mailInfo.setContent(msuggestions);
            // 这个类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            return sms.sendTextAndFileMail(mailInfo,filePath,pathIndex);// 发送文体格式
        }
}
