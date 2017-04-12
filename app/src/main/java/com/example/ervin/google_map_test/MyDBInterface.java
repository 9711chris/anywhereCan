package com.example.ervin.google_map_test;

/**
 * Created by Ervin on 09/04/2017.
 */

public interface MyDBInterface {
    public void insertIntoPlan(Plan plan);
    public Plan retrievePlan(int id);
}
