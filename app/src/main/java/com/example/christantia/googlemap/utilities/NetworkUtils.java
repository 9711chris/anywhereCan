package com.example.christantia.googlemap.utilities;

import android.content.Context;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by felix on 4/6/2017.
 */

public final class NetworkUtils {

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void downloadKmlZip (Context context, URL url, String fName) throws IOException {
        InputStream is = url.openStream();
        DataInputStream dis = new DataInputStream(is);
        byte[] buffer = new byte[1024];
        int length;

        FileOutputStream fos = context.openFileOutput(fName + ".zip", Context.MODE_PRIVATE);
        while ((length = dis.read(buffer))>0) {
            fos.write(buffer, 0, length);
        }
    }
}
