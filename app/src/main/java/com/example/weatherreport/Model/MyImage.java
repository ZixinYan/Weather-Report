package com.example.weatherreport.Model;


import android.graphics.Bitmap;


public class MyImage {
    private int imageId;
    private String name;
    private Bitmap bm;
    public MyImage(String _name, Bitmap _bm){
        name=_name;
        bm=_bm;
    }
    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
