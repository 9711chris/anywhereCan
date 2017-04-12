package com.example.ervin.google_map_test;

/**
 * Created by Ervin on 07/04/2017.
 */

public class Destination {
    private int id;
    private double longitude;
    private double latitude;
    public String name;
    public String type;

    public Destination (int id, double longitude, double latitude, String name, String type) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.type = type;
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
