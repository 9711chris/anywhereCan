package com.example.christantia.googlemap.model;

/**
 * Created by Christantia on 4/12/2017.
 */

import com.example.christantia.googlemap.data.LocationsDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.route.Segment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by Ervin on 07/04/2017.
 */

public class Planner {
    private ArrayList<Plan> planList = new ArrayList<>(5);
    private MyDBInterface mydatabase;

    public Planner(LocationsDbHelper dbHelper) {

        for(int i=0;i<4;i++)
        {
            planList.add(new Plan(i));
        }
            mydatabase = new MySQLiteDB(dbHelper);
    }

    public ArrayList<Plan> getPlanList() {
        return planList;
    }

    public void addDestinationToPlan(int planId, int destinationId) {
        if (planId < 0 || planId > 3) return;

        planList.get(planId).addDestinationId(destinationId);
        mydatabase.insertIntoPlan(planList.get(planId));

    }

    public boolean deleteDestinationFromPlan(int planId, int destinationId) {
        if (planId < 0 || planId > 3) return false;
        planList.get(planId).removeDestinationId(destinationId);
        mydatabase.insertIntoPlan(planList.get(planId));
        return true;
    }

    public void findEfficientRoute(int planId) {

    }

    public void createManualRoute(int planId, ArrayList<Integer> destinationIds) {
        planList.get(planId).setDestinationIds(destinationIds);
        mydatabase.insertIntoPlan(planList.get(planId));
    }

}
