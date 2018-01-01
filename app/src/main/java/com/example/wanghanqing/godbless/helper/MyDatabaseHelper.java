package com.example.wanghanqing.godbless.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wanghanqing.godbless.values.Values;

/**
 * Created by wanghanqing on 2017/12/17.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {


    private final static String dbname = Values.SENSORPATH + "experiment.db3";
    private final static int version = 1;
    private final String CREATE_labtable_SQL="create table EXP_lab_table (EXP_ID integer primary key " +
            "autoincrement, tester text, facility text, expression text)";
    private final String CREATE_exptable_SQL="create table EXP_table (ID integer primary key " +
            "autoincrement, EXP_ID integer, GX real, GY real, PX real, PY real, PRDid text)";

    public MyDatabaseHelper(Context context) {
        super(context, dbname, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_labtable_SQL);
        sqLiteDatabase.execSQL(CREATE_exptable_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}