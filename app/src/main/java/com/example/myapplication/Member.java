package com.example.myapplication;

public class Member {

    private String name;
    private String gend;
    private String license;
    private String address;
    private String violations;
    private String latitude;
    private String longitude;
    private String datetime;


    public Member() {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGend() {
        return gend;
    }

    public void setGend(String gend) {
        this.gend = gend;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
