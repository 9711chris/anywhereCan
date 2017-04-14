package com.example.christantia.googlemap.data;

import com.example.christantia.googlemap.model.Plan;

/**
 * Created by Christantia on 4/12/2017.
 */

public interface MyDBInterface {
    public void insertIntoPlan(Plan plan);
    public Plan retrievePlan(int id);
}