package com.example.wanghanqing.godbless.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.values.Values;


public class PrimeActivity extends AppCompatActivity {
    public Button creatb;
    public Button xzdb;
    public Button PDRexp;
    public Button GPSTra;
    public SQLiteDatabase dbPDR;
    public SQLiteDatabase dbGPS;
    public static String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);
        creatb = (Button) findViewById(R.id.creatb);
        xzdb = (Button) findViewById(R.id.xzdb);
        PDRexp = (Button) findViewById(R.id.PDRexp);
        GPSTra = (Button) findViewById(R.id.GPSTra);

        //创建或打开数据库
        dbPDR = SQLiteDatabase.openOrCreateDatabase(Values.SENSORPATH + "PDRData.db3", null);
        dbGPS = SQLiteDatabase.openOrCreateDatabase(Values.SENSORPATH + "GPSData.db3", null);

        PDRexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrimeActivity.this, ChooseActivity.class);
                startActivity(intent);
            }
        });

        GPSTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrimeActivity.this, GPSTrackActivity.class);
                startActivity(intent);
            }
        });

        creatb.setOnClickListener(new creatbListener());
    }

    final class creatbListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater factory = LayoutInflater.from(view.getContext());
            final View dialogView = factory.inflate(R.layout.dialog_sensorsave, null);
            builder.setTitle("请输入数据表/轨迹名称");
            builder.setView(dialogView);
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) dialogView.findViewById(R.id.message);// 保存info的Edittext
                            tableName = editText.getText().toString();
                            dbPDR.execSQL("create table " + tableName + "SENSOR" + "(" +
                                    "SID integer primary key autoincrement ," +
                                    " time timestamp NOT NULL DEFAULT(datetime('now','localtime')), " +
                                    " acc1 real , acc2 real , acc3 real , ori real , gyro1 real , gyro2 real , gyro3 real)");
                            dbPDR.execSQL("create table " + tableName + "(" +
                                    "PID integer primary key autoincrement ," +
                                    " PX real , PY real )");
                            dbGPS.execSQL("create table " + tableName + "(" +
                                    "GID integer primary key autoincrement ," +
                                    " GX real , GY real )");
                            Toast.makeText(PrimeActivity.this, "已成功创建" + tableName + "数据表",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.show();
        }
    }
}
