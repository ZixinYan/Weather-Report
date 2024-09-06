package com.example.weatherreport.Model;

public class Today {
    private String shidu;
    private String PM25;
    private String PM10;
    private String quality;
    private String wendu;
    private String ganmao;


    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPM25() {
        return PM25;
    }

    public void setPM25(String PM25) {
        this.PM25 = PM25;
    }

    public String getPM10() {
        return PM10;
    }

    public void setPM10(String PM10) {
        this.PM10 = PM10;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getGanmao() {
        return ganmao;
    }

    public void setGanmao(String ganmao) {
        this.ganmao = ganmao;
    }

    @Override
    public String toString(){
        return "Today{" +
                "shidu='" + shidu + '\'' +
                "PM25='" + PM25 + '\'' +
                "PM10='" + PM10 + '\'' +
                "quality='" + quality + '\'' +
                "wendu='" + wendu + '\'' +
                "ganmao='" + ganmao + '\'' +
                "}";
    }
}
