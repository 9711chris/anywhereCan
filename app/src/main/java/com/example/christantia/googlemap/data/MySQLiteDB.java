package com.example.christantia.googlemap.data;

/**
 * Created by Christantia on 4/12/2017.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.christantia.googlemap.model.Plan;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by Ervin on 07/04/2017.
 */

public class MySQLiteDB implements MyDBInterface {

    private SQLiteDatabase mydatabase;

    public MySQLiteDB(LocationsDbHelper dbHelper) {
        mydatabase = dbHelper.getWritableDatabase();
    }

    /*
        If id not exist, create plan
        otherwise, update plan
     */
    public void insertIntoPlan(Plan plan) {
        mydatabase.execSQL("DELETE FROM planLocationRelations WHERE planId = " + plan.getId());

        for (int i=0; i<plan.getDestinationIds().size(); i++) {
            int did = plan.getDestinationIds().get(i);
         //   mydatabase.execSQL("INSERT INTO planLocationRelations VALUES(" + plan.getId() + ", " + did + ", " +  i + ");");
            ContentValues values = new ContentValues();
            values.put(LocationsContract.PlanLocationsEntry
                    .COLUMN_PLAN_ID, plan.getId() );
            values.put(LocationsContract.PlanLocationsEntry
                    .COLUMN_LOCATION_ID, did);
            values.put(LocationsContract.PlanLocationsEntry
                    .COLUMN_SEQ, i);
            mydatabase.insert(LocationsContract.PlanLocationsEntry.TABLE_NAME, null, values);
        }
    }


    public Plan retrievePlan(int id) {
        if (id < 0 || id > 3) {
            return new Plan();
        }
        //Cursor cursor = mydatabase.rawQuery("SELECT * FROM planLocationRelations WHERE planId = " + id + " ORDER BY seq ASC", null);
        Cursor cursor = mydatabase.query(false, LocationsContract.PlanLocationsEntry.TABLE_NAME,null,"planId = "+id ,null,null,null,"seq ASC",null);
        ArrayList<Integer> destinationIds = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            destinationIds.add(cursor.getInt(2));
        }
        cursor.close();

        return new Plan(id, destinationIds);
    }

}
