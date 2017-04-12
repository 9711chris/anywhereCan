package com.example.christantia.googlemap.utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by felix on 4/9/2017.
 */

public class FilesUtils {

    public static void unzip(Context context, String filename) throws IOException {
        File zipFile = new File(context.getFilesDir(), filename);
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                String location = context.getFilesDir().toString();
                String path = location + '/' + ze.getName();
                Log.d("zipPath: ", path);
                if (ze.isDirectory()) {
                    File unzipFile = new File(path);
                    if(!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                }
                else {
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    }
                    finally {
                        fout.close();
                    }
                }
            }
        }
        finally {
            zin.close();
        }
    }

    public static String readKmlContent(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        scanner.useDelimiter("\\A");
        boolean hasInput = scanner.hasNext();
        if(hasInput) {
            return scanner.next();
        } else {
            return null;
        }

    }

}
