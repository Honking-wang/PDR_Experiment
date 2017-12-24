package com.example.wanghanqing.godbless.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;

import com.example.wanghanqing.godbless.view.AoMyView;

import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;

public class ExpPDRActivity extends Activity {

    public Button startpdr;
    public Button endpdr;
    public TextView text;
    Intent intent;


    public int lentype;
    public int oritype;


    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];
    float[] values = new float[3];

    private SensorManager sensorManager = null;
    private Sensor sensor_ACCELEROMETER = null;
    private Sensor sensor_MAGNETIC = null;

    private MyOri1SensorEventListener myOri1SensorEventListener;

    public float orient;
    public float length;

    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;

    public AoMyView pdraoMyView;
    public static WorkSpace pdrworkSpace;
    public static AoMap pdraoMap;
    public static LocationManager pdrlocationManager;
    public String pdrprovider;
    public static Location pdrlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exppdr);
        //打开地图
        AoSysLib.loadLib(LIBPATH);
        pdraoMyView = findViewById(R.id.pdraomyview);
        pdraoMyView.setActivity(this);
        pdraoMap = new AoMap();
        pdraoMap.openMap(GPJPATH);
        pdraoMyView.setMap(pdraoMap);
        //显示地图
        pdraoMyView.resetView();
        pdraoMyView.zoomView(-480, 800, 6);
        pdraoMyView.updateView();
        pdraoMyView.updateView();

        intent = getIntent();
        lentype = Integer.parseInt(intent.getStringExtra("lentype"));
        oritype = Integer.parseInt(intent.getStringExtra("oritype"));
        startpdr = findViewById(R.id.startpdr);
        endpdr = findViewById(R.id.endpdr);
        text = findViewById(R.id.text);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        startpdr.setOnClickListener(new startpdrListener());

    }

    final class startpdrListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            getorient();
            length = getlength();
            text.setText(length+"");

        }
    }

    public void getorient() {
        switch (oritype) {
            case 1:
                myOri1SensorEventListener = new MyOri1SensorEventListener();
                sensorManager.registerListener(myOri1SensorEventListener, sensor_ACCELEROMETER,
                        SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(myOri1SensorEventListener, sensor_MAGNETIC,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;
        }

    }

    public float getlength() {
        float len=0;
        switch (lentype) {
            case 1:
                len = 0.7f;
                break;
            case 2:
                break;
            case 3:
                break;
        }
        return len;
    }


    final class MyOri1SensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();

            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticfieldValues = sensorEvent.values.clone();
            }
            SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticfieldValues);
            SensorManager.getOrientation(r, values);
            orient = (float) Math.toDegrees(values[0]);
            if (orient < 0) {
                orient += 360;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {


        }
    }
}






