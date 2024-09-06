package com.example.weatherreport.Model;

public class city {
    private String cityName;
    private String code;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString(){
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
