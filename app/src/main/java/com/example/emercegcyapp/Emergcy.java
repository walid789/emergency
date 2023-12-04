package com.example.emercegcyapp;

public class Emergcy {
private  int id,id_user ;
private String date  ;
private float  Latitude,Longitude;
 private String cas;



    public Emergcy(int id, int id_user, String date, float latitude, float longitude , String cas) {
        this.id = id;
        this.id_user = id_user;
        this.date = date;
        Latitude = latitude;
        Longitude = longitude;
        this.cas=cas;
    }

    @Override
    public String toString() {
        return "Emergcy =" +
                ", cas ='" + cas + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }
}
