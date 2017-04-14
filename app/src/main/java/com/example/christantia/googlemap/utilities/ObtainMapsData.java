package com.example.christantia.googlemap.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.christantia.googlemap.data.LocationsContract;
import com.example.christantia.googlemap.data.LocationsDbHelper;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by felix on 4/8/2017.
 */

public class ObtainMapsData {

    public static Map<String,String> urlStrings = createMap();
    private static Map<String, String> createMap()
    {
        Map<String,String> myMap = new HashMap<>();
        myMap.put("hawker-centres", "https://data.gov.sg/api/action/package_metadata_show?id=hawker-centres");
        myMap.put("hotels", "https://data.gov.sg/api/action/package_metadata_show?id=hotels");
        myMap.put("parks", "https://data.gov.sg/api/action/package_metadata_show?id=parks");
        myMap.put("museums", "https://data.gov.sg/api/action/package_metadata_show?id=museums");
        myMap.put("sportsfieldssg", "https://data.gov.sg/api/action/package_metadata_show?id=sportsfieldssg");
        return myMap;
    }

    public static URL getDownloadUrlFromApi(String urlString) {
        URL url = null, downloadUrl=null;
        String jsonStrResponse = null, downloadUrlStr = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            jsonStrResponse = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            downloadUrlStr = JsonXmlUtils.getDownloadUrlFromJson(jsonStrResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            downloadUrl = new URL(downloadUrlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return downloadUrl;
    }

    public static void downloadAndUnzipKml(Context context, String zipName) {
        URL downloadUrl = getDownloadUrlFromApi(urlStrings.get(zipName));
        try {
            NetworkUtils.downloadKmlZip(context, downloadUrl, zipName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FilesUtils.unzip(context, zipName + ".zip");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> listKmlFiles(Context context) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : ObtainMapsData.urlStrings.entrySet()){
            Log.d("FName: ", entry.getKey());
            ObtainMapsData.downloadAndUnzipKml(context, entry.getKey());
        }
        File appDirectory = new File(context.getFilesDir().toString());
        File[] files = appDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            Log.d("DownloadedFile: ", fileName);
            if (fileName.endsWith(".kml")) result.add(fileName);
        }
        return result;
    }

    private static void getAddressData(Context context, SQLiteDatabase db){
        Cursor resultSet = db.rawQuery("SELECT * FROM " + LocationsContract.LocationsEntry.TABLE_NAME, null);
        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            String name = resultSet.getString(1);
            String category = resultSet.getString(2);
            String coordinates = resultSet.getString(3);

            System.out.println("ANJENG NAME: " + name);
            System.out.println("ANJENG COORDINATES: " + coordinates);

            String longitude = coordinates.split(",")[0];
            String latitude = coordinates.split(",")[1];

            System.out.println("ANJENG LOKASI: " + latitude + " " + longitude);
            String address = resultSet.getString(4);
            System.out.println("ANJENG adress = " + address);
            if (address == null) {

                Geocoder geoCoder = new Geocoder(context);
                List<Address> matches = null;
                try {
                    matches = geoCoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                if (bestMatch != null)
                    address = bestMatch.getAddressLine(0);
                System.out.println("ANJENG new address = " + address);
                db.execSQL("UPDATE " + LocationsContract.LocationsEntry.TABLE_NAME +
                        " SET address = \"" + address + "\"" +
                        " WHERE " + LocationsContract.LocationsEntry._ID + " = " + resultSet.getInt(0));

            }
            resultSet.moveToNext();
        }
    }

    public static void saveAllKmlToDb(Context context, LocationsDbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor resultSet = db.rawQuery("SELECT * FROM " + LocationsContract.LocationsEntry.TABLE_NAME, null);
        resultSet.moveToFirst();

        if (resultSet.isAfterLast()) {
            for (String filename : listKmlFiles(context)) {
                try {
                    String kmlString = FilesUtils.readKmlContent(context, filename);
                    for (Map<String, String> entryData : JsonXmlUtils.extractLocationDataXml(kmlString)) {
                        ContentValues values = new ContentValues();
                        values.put(LocationsContract.LocationsEntry
                                .COLUMN_LOCATION_NAME, entryData.get(LocationsContract.LocationsEntry.COLUMN_LOCATION_NAME));
                        values.put(LocationsContract.LocationsEntry
                                .COLUMN_LOCATION_TYPE, entryData.get(LocationsContract.LocationsEntry.COLUMN_LOCATION_TYPE));
                        values.put(LocationsContract.LocationsEntry
                                .COLUMN_COORDINATES, entryData.get(LocationsContract.LocationsEntry.COLUMN_COORDINATES));
                        db.insert(LocationsContract.LocationsEntry.TABLE_NAME, null, values);
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            getAddressData(context,db);
        }
    }
}
