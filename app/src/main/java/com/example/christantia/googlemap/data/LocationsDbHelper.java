package com.example.christantia.googlemap.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.christantia.googlemap.data.LocationsContract.*;
/**
 * Created by felix on 4/9/2017.
 */

public class LocationsDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "locations.db";

    private static final int DATABASE_VERSION = 2;

    public LocationsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATIONS_TABLE = "CREATE TABLE " + LocationsEntry.TABLE_NAME + " (" +
                LocationsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationsEntry.COLUMN_LOCATION_NAME + " TEXT NOT NULL, " +
                LocationsEntry.COLUMN_LOCATION_TYPE + " TEXT NOT NULL, " +
                LocationsEntry.COLUMN_COORDINATES + "  TEXT NOT NULL, " +
                LocationsEntry.COLUMN_ADDRESS + " TEXT DEFAULT NULL" +
                "); ";

        final String SQL_CREATE_PLANS_TABLE = "CREATE TABLE " + PlansEntry.TABLE_NAME + " (" +
                PlansEntry._ID + " INTEGER PRIMARY KEY" +
                "); ";

        final String SQL_CREATE_PLAN_LOCATION_RELATIONS_TABLE = "CREATE TABLE " +
                PlanLocationsEntry.TABLE_NAME + " (" +
                PlanLocationsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlanLocationsEntry.COLUMN_PLAN_ID + " INTEGER NOT NULL, " +
                PlanLocationsEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL, " +
                PlanLocationsEntry.COLUMN_SEQ + " INTEGER," +
                " FOREIGN KEY (" + PlanLocationsEntry.COLUMN_PLAN_ID + ") REFERENCES " +
                PlansEntry.TABLE_NAME + "(" + PlansEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                " FOREIGN KEY (" + PlanLocationsEntry.COLUMN_LOCATION_ID + ") REFERENCES " +
                LocationsEntry.TABLE_NAME + "(" + LocationsEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                "); ";

        db.execSQL(SQL_CREATE_LOCATIONS_TABLE);
        db.execSQL(SQL_CREATE_PLANS_TABLE);
        db.execSQL(SQL_CREATE_PLAN_LOCATION_RELATIONS_TABLE);

        db.execSQL("INSERT INTO plans (" + PlansEntry._ID+") VALUES (0)");
        db.execSQL("INSERT INTO plans (" + PlansEntry._ID+") VALUES (1)");
        db.execSQL("INSERT INTO plans (" + PlansEntry._ID+") VALUES (2)");
        db.execSQL("INSERT INTO plans (" + PlansEntry._ID+") VALUES (3)");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlansEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlanLocationsEntry.TABLE_NAME);

        onCreate(db);
    }
}
