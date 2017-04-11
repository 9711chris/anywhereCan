package com.example.christantia.googlemap.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by felix on 4/8/2017.
 */

public class JsonXmlUtils {

    public static String getDownloadUrlFromJson(String resourceJsonStr) throws JSONException {
        String downloadUrlStr;
        JSONObject resourceJson = new JSONObject(resourceJsonStr);

        if (resourceJson.has("success")) {
            if (!resourceJson.getBoolean("success")) {
                return null;
            }
        }

        JSONArray resourceArr = resourceJson.getJSONObject("result").getJSONArray("resources");

        for (int i = 0; i < resourceArr.length(); i++) {
            String resourceFormat = resourceArr.getJSONObject(i).getString("format");
            if (resourceFormat.equals("KML")) {
                return resourceArr.getJSONObject(i).getString("url");
            }
        }

        return null;
    }

    public static List<Map<String, String>> extractLocationDataXml(String xmlString)
            throws XmlPullParserException, IOException {
        boolean flag = false;
        String locationType = null;
        List<Map<String,String>> mapList = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( xmlString ) );
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType  == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("name")){
                locationType = xpp.nextText();
            }
            else if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("folder")){
                int eventType2 = xpp.next();
                while (eventType2 != XmlPullParser.END_DOCUMENT){
                    if (eventType2  == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("placemark")){
                        flag=false;
                        while (!flag) {
                            int eventType3 = xpp.next();
                            if (eventType3 == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("name")){
                                String locationName = xpp.nextText();

                                while (!flag) {
                                    int eventType4 = xpp.next();
                                    if (eventType4 == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("coordinates")){
                                        String coordinates = xpp.nextText();

                                        Map<String, String> tempMap = new HashMap<>();
                                        tempMap.put("locationName", locationName);
                                        tempMap.put("coordinates", coordinates);
                                        tempMap.put("locationType", locationType);
                                        mapList.add(tempMap);

                                        flag=true;
                                    }
                                }
                            }
                        }
                    }


                    eventType2 = xpp.next();
                }
            }
            eventType = xpp.next();
        }
        return mapList;
    }
}
