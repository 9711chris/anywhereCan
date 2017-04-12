package com.example.christantia.googlemap.model;

/**
 * Created by Christantia on 4/12/2017.
 */

public class Destination {
    private int id;
    private double longitude;
    private double latitude;
    private String address;
    public String name;
    public String type;

    public Destination (int id, double longitude, double latitude, String address, String name, String type) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.name = name;
        this.type = type;
    }

    public int getId(){
        return id;
    }

    public String getAddress(){
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
