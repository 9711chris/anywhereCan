package com.example.christantia.googlemap;

/**
 * Created by shelinalusandro on 5/4/17.
 */

public class DestinationItem {
    private String destinationName;
    private String latitude;
    private String longitude;

    public DestinationItem(String destinationName, String latitude, String longitude){
        this.destinationName = destinationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDestinationName(){
        return destinationName;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String toString(){
        return destinationName;
    }
}
