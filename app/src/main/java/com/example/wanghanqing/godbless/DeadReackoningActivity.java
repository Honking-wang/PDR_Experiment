package com.example.wanghanqing.godbless;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.AoGIS.database.AoMap;
import com.AoGIS.render.AoSysLib;

import java.util.ArrayList;

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class DeadReackoningActivity extends AppCompatActivity {


    public float len=0.7f;
    public int step;
    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];
    private float orient;
    public float orient0;
    public ArrayList<Float> orientList;

    public static boolean flag;
    public int num;


    public AoPDRView drView;
    public static AoMap aodrMap;

    public Button pdrstart;

    private SensorManager sensorManager = null;
    private Sensor sensor_DETECTOR = null;
    private Sensor sensor_ACCELEROMETER = null;
    private Sensor sensor_MAGNETIC = null;

    private MySensorEventListener mySensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadreackoning);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        drView = (AoPDRView) findViewById(R.id.dr_mapview);
        drView.setActivity(this);
        aodrMap = new AoMap();
        aodrMap.openMap(GPJPATH);
        drView.setMap(aodrMap);//指定mapView显示map代表的地图文件

        //显示地图
        drView.resetView();
        drView.zoomView(-480, 800, 6);
        drView.updateView();

        pdrstart = (Button) findViewById(R.id.pdrstart);
        orientList = new ArrayList();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mySensorEventListener = new MySensorEventListener();

        pdrstart.setOnClickListener(new PDRstartListener());

    }

    final class PDRstartListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!flag) {
                flag = true;
                sensorManager.registerListener(mySensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                flag = false;
                sensorManager.unregisterListener(mySensorEventListener, sensor_ACCELEROMETER);
                sensorManager.unregisterListener(mySensorEventListener, sensor_DETECTOR);
                sensorManager.unregisterListener(mySensorEventListener, sensor_MAGNETIC);
            }
        }
    }


    final class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    step++;
                    for (int i = 0; i < num; i++) {
                        orient0 = orient0 + orientList.get(i);
                    }
                    orient0 = orient0 / num;



                    orientList = new ArrayList();
                    num = 0;
                    orient0 = 0;


                } else {
                    orientList.add(orient);
                    num += 1;
                }
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticfieldValues = sensorEvent.values.clone();
                SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticfieldValues);
                SensorManager.getOrientation(r, values);
                orient = (float) Math.toDegrees(values[0]);
                if (orient < 0) {
                    orient += 360;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
