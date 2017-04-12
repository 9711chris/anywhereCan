package com.example.ervin.google_map_test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ervin.google_map_test.DB.LocationsContract;
import com.example.ervin.google_map_test.DB.LocationsDbHelper;

import android.database.sqlite.SQLiteDatabase.CursorFactory.*;

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
            mydatabase.execSQL("INSERT INTO planLocationRelations VALUES(" + plan.getId() + ", " + did + ", " +  i + ");");
        }
    }


    public Plan retrievePlan(int id) {
        if (id < 1 || id > 4) {
            return new Plan();
        }
        //Cursor cursor = mydatabase.rawQuery("SELECT * FROM planLocationRelations WHERE planId = " + id + " ORDER BY seq ASC", null);
        Cursor cursor = mydatabase.query(false, LocationsContract.PlanLocationRelationsEntry.TABLE_NAME,null,"planId = "+id ,null,null,null,"seq ASC",null);
        ArrayList<Integer> destinationIds = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            destinationIds.add(cursor.getInt(1));
        }
        cursor.close();

        return new Plan(id, destinationIds);
    }

}
