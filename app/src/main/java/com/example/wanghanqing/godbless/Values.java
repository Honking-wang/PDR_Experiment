package com.example.wanghanqing.godbless;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by wanghanqing on 2017/6/21.
 */

public class Values {
    public static String LIBPATH = Environment.getExternalStorageDirectory() + "/JFZYMap/AoGIS/";
    public static String GPJPATH = Environment.getExternalStorageDirectory() + "/JFZYMap/JFZYMap.GPJ";
    public static String SENSORPATH = Environment.getExternalStorageDirectory() + "/JFZYMap/Sensor/";

    public static boolean saveSensor(String path,String name, String content) {
        try {

            File file = new File(path,name);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
