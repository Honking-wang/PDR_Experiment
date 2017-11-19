package com.example.wanghanqing.godbless;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by wanghanqing on 2017/11/4.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {



//    public static final String ptable="create table"+PDRTrackActivity.name
//            +"(id integer autoincrement,"
//            +"time text,step integer,orient real,mag1 real,mag2 real,mag3 real,acc1 real,"
//            +"acc2 real,acc3 real)";

    public static final String ptable ="create table Book ("
            +"id integer primary key autoincrement,"
            +"author text,"
            +"price real,"
            +"pages integer,"
            +"name text)";



    private Context mcontext;
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ptable);
        Toast.makeText(mcontext, "Create succeeded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
