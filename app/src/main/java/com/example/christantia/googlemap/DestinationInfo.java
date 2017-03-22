package com.example.christantia.googlemap;

/**
 * Created by Christantia on 3/21/2017.
 */

public class DestinationInfo {
    private int pictureId;
    private String name;
    private String address;
    private String openingHour;

    public DestinationInfo(int pictureId, String name, String address, String openingHour){
        this.pictureId = pictureId;
        this.name = name;
        this.address = address;
        this.openingHour = openingHour;
    }
    public int getPictureId(){
        return pictureId;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public String getOpeningHour(){
        return openingHour;
    }
}
