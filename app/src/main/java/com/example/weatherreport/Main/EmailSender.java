package com.example.weatherreport.Main;

import static com.example.weatherreport.Data.config.CONFIG_PHOTO_AUTHORITY;
import static com.example.weatherreport.Data.config.CONFIG_SEND_SMS;
import static com.example.weatherreport.Data.config.EMAIL;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherreport.Data.EmailUtil;
import com.example.weatherreport.Data.ImageAdapter;
import com.example.weatherreport.Data.RealPathFromUriUtils;
import com.example.weatherreport.Model.MyImage;
import com.example.weatherreport.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


    public class EmailSender extends AppCompatActivity {
        /**
         * 获取相册uri等
         */
        String[] mPermissionList = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        public static final int REQUEST_PICK_IMAGE = 11101;

        EditText editText;
        Button addImageButton;
        Button sendEmail;
        SharedPreferences sharedPreferences;
        private List<MyImage> list=new ArrayList<>();

        String[] filePath;
        int pathIndex=0;
        final int maxEmailNum=20;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_email_sender);
            sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
            View view = findViewById(R.id.activity_email_sender);
            view.setBackgroundColor(Color.parseColor(sharedPreferences.getString("color","#C8E0F3")));

            SetData();

        }

        private void SetData() {
            filePath=new String[maxEmailNum+1];
            editText=(EditText)findViewById(R.id.email_editText);
            addImageButton=(Button)findViewById(R.id.add_image_button);
            sendEmail=(Button)findViewById(R.id.send_button);

            addImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(EmailSender.this, mPermissionList, CONFIG_PHOTO_AUTHORITY);
                }
            });
            sendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBeforeSendEmail();
                }
            });
        }
        private void CheckBeforeSendEmail(){
            if (ContextCompat.checkSelfPermission(EmailSender.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EmailSender.this, new String[]{ Manifest.permission. SEND_SMS }, CONFIG_SEND_SMS);
            } else {
                SendEmail();
            }
        }
        private void SendEmail() {
            final String message=editText.getText().toString();
            final String title="App反馈";
            boolean flag=false;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    EmailUtil.SendTextAndFileMail(title,message,EMAIL,filePath,pathIndex);
                    Intent intent =new Intent("com.example.studyapp.EmailSendBroadcast");
                    sendOrderedBroadcast(intent,null);
                    for(int i=0;i<pathIndex;i++)
                    {
                        filePath[i]="";
                    }
                    pathIndex=0;
                }
            }).start();
            finish();
        }

        private void GetImage() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),REQUEST_PICK_IMAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICK_IMAGE);//REQUEST_PICK_IMAGE与onActivityResult()函数中对应
            }
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_PICK_IMAGE:
                        if (data != null) {
                            final String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());//获取真正路径
                            if(pathIndex>=maxEmailNum)
                                Toast.makeText(this, "添加附件过多", Toast.LENGTH_SHORT).show();
                            else{
                                filePath[pathIndex++]=realPathFromUri;
                                Toast.makeText(this, "图片选择成功", Toast.LENGTH_SHORT).show();
                                File file = new File(realPathFromUri);
                                MyImage image;
                                if(file.exists()){
                                    Bitmap bm = BitmapFactory.decodeFile(realPathFromUri);
                                    image=new MyImage(realPathFromUri,bm);
                                }else{
                                    image=new MyImage(realPathFromUri,null);
                                }

                                list.add(image);
                                QueryImage();
                            }

                        } else {
                            Toast.makeText(this, "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        }

        private void QueryImage() {
            ImageAdapter adapter=new ImageAdapter(EmailSender.this,R.layout.image_item,list);
            ListView listView=(ListView)findViewById(R.id.send_listview);
            listView.setAdapter(adapter);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case CONFIG_PHOTO_AUTHORITY:
                    boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
                        GetImage();
                    } else {
                        Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CONFIG_SEND_SMS:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SendEmail();
                    } else {
                        Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
            }
        }
    }
