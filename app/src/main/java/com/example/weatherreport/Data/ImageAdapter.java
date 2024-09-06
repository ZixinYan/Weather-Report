package com.example.weatherreport.Data;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherreport.Model.MyImage;
import com.example.weatherreport.R;

import java.util.List;

/**
 * 自定义适配器
 * 发送邮件时选择图片，下方显示图片列表
 */

public class ImageAdapter extends ArrayAdapter<MyImage>{
    private int resourceId;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyImage myImage=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView imageView=(ImageView)view .findViewById(R.id.image_imageview);
        TextView textView=(TextView)view.findViewById(R.id.image_textview);
        imageView.setImageBitmap(myImage.getBm());
        textView.setText(myImage.getName());
        return view;
    }

    public ImageAdapter(Context context, int textviewResourceId, List<MyImage> objects){
        super(context,textviewResourceId,objects);
        resourceId=textviewResourceId;
    }
}
