package com.example.weatherreport.Model;

public class address {
    private String city;
    private String direction;
    private String distance;
    private String district;
    private String province;
    private String street;

    @Override
    public String toString(){
        return "address{" +
                "city='" + city + '\'' +
                "direction='" + direction + '\'' +
                "distance='" + distance + '\'' +
                "district='" + district + '\'' +
                "province='" + province + '\'' +
                "street='" + street + '\'' +
                "}";
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }



}
