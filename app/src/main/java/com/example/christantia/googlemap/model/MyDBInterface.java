package com.example.christantia.googlemap.model;

/**
 * Created by Christantia on 4/12/2017.
 */

public interface MyDBInterface {
    public void insertIntoPlan(Plan plan);
    public Plan retrievePlan(int id);
}