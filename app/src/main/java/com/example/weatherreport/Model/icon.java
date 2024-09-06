package com.example.weatherreport.Model;

public class icon {
    private String weather;
    private String Location;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString(){
        return "icon{" +
                "weather='" + weather + '\'' +
                "Location='" + Location + '\'' +
                "}";
    }


    public void setLocation(String location) {
        Location = location;
    }
    public String getLocation() {
        return this.Location;
    }
}
