package com.example.christantia.googlemap;

/**
 * Created by Christantia on 3/21/2017.
 */

public class DestinationInfo {
    // private int pictureId;
    private String name;
    private String address;

    public DestinationInfo(String name, String address){
        // this.pictureId = pictureId;
        this.name = name;
        this.address = address;
    }

    // public int getPictureId(){ return pictureId; }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
}
