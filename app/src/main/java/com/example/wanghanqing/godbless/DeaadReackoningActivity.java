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

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class DeaadReackoningActivity extends AppCompatActivity {


    public int step;
    public boolean flag;

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

        pdrstart= (Button) findViewById(R.id.pdrstart);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mySensorEventListener = new MySensorEventListener();

        pdrstart.setOnClickListener(new PDRstartListener());

    }

    final class PDRstartListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (!flag) {
                sensorManager.registerListener(mySensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR, SensorManager.SENSOR_DELAY_FASTEST);
                flag=true;
            }else{
                sensorManager.unregisterListener(mySensorEventListener,sensor_ACCELEROMETER);
                sensorManager.unregisterListener(mySensorEventListener,sensor_DETECTOR);
                sensorManager.unregisterListener(mySensorEventListener,sensor_MAGNETIC);
                flag=false;
            }
        }
    }




    final class MySensorEventListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            }
            if (sensorEvent.sensor.getType()==Sensor.TYPE_STEP_DETECTOR){
                if (sensorEvent.values[0] == 1.0) {
                    step++;











                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
