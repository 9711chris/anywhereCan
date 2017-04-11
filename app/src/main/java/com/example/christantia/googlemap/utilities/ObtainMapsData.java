package com.example.christantia.googlemap.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

    private static Context context;

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

    public static void saveAllKmlToDb(Context context, LocationsDbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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
    }
}
