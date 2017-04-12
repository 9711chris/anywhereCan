package com.example.christantia.googlemap.model;

import java.util.ArrayList;

/**
 * Created by Christantia on 4/12/2017.
 */

public class Plan {
    private int id;
    private ArrayList<Integer> destinationIds = new ArrayList<>();


    public Plan() {
        id = -1;
    }

    public Plan(int id) {
        this.id = id;
    }

    public Plan(int id, ArrayList<Integer> destinationIds) {
        this.id = id;
        this.destinationIds = destinationIds;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getDestinationIds() {
        return destinationIds;
    }

    public void setDestinationIds(ArrayList<Integer> destinationIds) {
        this.destinationIds = destinationIds;
    }

    public void addDestinationId(int destinationId) {
        destinationIds.add(destinationId);

    }

    public boolean removeDestinationId(int destinationId) {
        for (int i=0; i<destinationIds.size(); ++i) {
            if (destinationIds.get(i) == destinationId) {
                destinationIds.remove(i);
                return true;
            }
        }

        return false;
    }
}
