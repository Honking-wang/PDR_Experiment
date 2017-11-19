package com.example.wanghanqing.godbless.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import com.AoGIS.database.AoMap;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.values.Values;
import com.example.wanghanqing.godbless.view.AoPDRView;


import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;

public class PDRTrackActivity extends AppCompatActivity {

    private float gyro1;
    private float gyro2;
    private float gyro3;
    private float gyro_unca1;
    private float gyro_unca2;
    private float gyro_unca3;
    private float gyro_unca4;
    private float gyro_unca5;
    private float gyro_unca6;
    private float acc1;
    private float acc2;
    private float acc3;
    private float mag1;
    private float mag2;
    private float mag3;

    private TextView timTextView;
    private TextView oriTextView;
    private TextView gyroTextView;
    private TextView gyro_uncaTextView;
    private TextView accTextView;
    private TextView magTextView;
    private TextView steTextView;

    private SensorManager sensorManager = null;
    private Sensor sensor_DETECTOR = null;
    private Sensor sensor_ACCELEROMETER = null;
    private Sensor sensor_MAGNETIC = null;
    private Sensor sensor_Oritation = null;
    private Sensor sensor_Gyroscope = null;
    private Sensor sensor_Gyroscope_uncalibrated = null;

    private MySensorEventListener mySensorEventListener;

    public SQLiteDatabase dbPDR;

    public double len = 0.7;
    public static int count;
    public int step;
    public static String name;
    public static double PX = 39443601;
    public static double PY = 4429854;
    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];
    private float orient;
    public float orient1;
    public static boolean flag;
    public int num;
    public AoPDRView drView;
    public static AoMap aodrMap;
    public Button pdrstart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deadreackoning2);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        drView = (AoPDRView) findViewById(R.id.dr_mapview2);
        drView.setActivity(this);
        aodrMap = new AoMap();
        aodrMap.openMap(GPJPATH);
        drView.setMap(aodrMap);//指定mapView显示map代表的地图文件

        //显示地图
        drView.resetView();
        drView.zoomView(-480, 800, 6);
        drView.updateView();
        pdrstart = (Button) findViewById(R.id.pdrstart2);

        timTextView = (TextView) this.findViewById(R.id.timed2);
        oriTextView = (TextView) this.findViewById(R.id.orientd2);
        gyroTextView = (TextView) findViewById(R.id.gyrod2);
        gyro_uncaTextView = (TextView) findViewById(R.id.gyrod21);
        accTextView = (TextView) findViewById(R.id.accd2);
        magTextView = (TextView) findViewById(R.id.magnd2);
        steTextView = (TextView) this.findViewById(R.id.stepd2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_Gyroscope_uncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        mySensorEventListener = new MySensorEventListener();

        pdrstart.setOnClickListener(new PDRstartListener());

        dbPDR = SQLiteDatabase.openOrCreateDatabase(Values.SENSORPATH + "PDRData.db3", null);
    }


    final class PDRstartListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!flag) {
                flag = true;
                pdrstart.setText("轨迹关闭");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
                timTextView.setText(sdf.format(new Date()));
                sensorManager.registerListener(mySensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_Oritation, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope_uncalibrated, sensorManager.SENSOR_DELAY_FASTEST);

            } else {
                sensorManager.unregisterListener(mySensorEventListener, sensor_ACCELEROMETER);
                sensorManager.unregisterListener(mySensorEventListener, sensor_MAGNETIC);
                sensorManager.unregisterListener(mySensorEventListener, sensor_DETECTOR);
                sensorManager.unregisterListener(mySensorEventListener, sensor_Oritation);
                sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope);
                sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope_uncalibrated);
                double[] points = new double[2 * (count + 1)];// 加入了当前位置，所以+1，COUNT不包括当前位置
                double[] zuobiao;
                for (int i = 0; i < count; i++) {
                    zuobiao = drView.List.get(i);
                    points[i * 2] = zuobiao[0];
                    points[i * 2 + 1] = zuobiao[1];
                }
                // 加入当前点
                points[2 * count] = PX;
                points[2 * count + 1] = PY;

                StringBuffer stringBuffer = new StringBuffer();

                for (int i = 0; i < count; i++) {// 无需加入当前位置的点
                    stringBuffer.append(points[2 * i] + " ");
                    stringBuffer.append(points[2 * i + 1] + "\n");
                }
                count = 0;
                flag = false;
                sensorManager.unregisterListener(mySensorEventListener, sensor_ACCELEROMETER);
                sensorManager.unregisterListener(mySensorEventListener, sensor_DETECTOR);
                sensorManager.unregisterListener(mySensorEventListener, sensor_MAGNETIC);
                pdrstart.setText("轨迹开启");
            }
        }
    }


    final class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();
                acc1 = sensorEvent.values[0];
                acc2 = sensorEvent.values[1];
                acc3 = sensorEvent.values[2];
                accTextView.setText(" acc1: " + sensorEvent.values[0] + " acc2: " + sensorEvent.values[1] +
                        " acc3: " + sensorEvent.values[2]);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    step++;
                    steTextView.setText("" + step);
                    orient1 = (float) (orient * Math.PI / 180);

                    PX = PX + len * Math.cos((5 * Math.PI / 2) - orient1);
                    PY = PY + len * Math.sin((5 * Math.PI / 2) - orient1);
                    insertpdrtodb(dbPDR, PX, PY);
                    //dbPDR.execSQL("insert into " + PrimeActivity.tableName + " ( PX , PY ) values (" + PX + " , " + PY + " );");
                    drView.updateView();
                    num = 0;

                } else {
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
                oriTextView.setText("" + orient);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyro1 = sensorEvent.values[0];
                gyro2 = sensorEvent.values[1];
                gyro3 = sensorEvent.values[2];
                gyroTextView.setText(" gyro1: " + gyro1 + " gyro2: " + gyro2 + " gyro3: " + gyro3);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                gyro_unca1 = sensorEvent.values[0];
                gyro_unca2 = sensorEvent.values[1];
                gyro_unca3 = sensorEvent.values[2];
                gyro_unca4 = sensorEvent.values[3];
                gyro_unca5 = sensorEvent.values[4];
                gyro_unca6 = sensorEvent.values[5];
                gyro_uncaTextView.setText(" gyro_unca1: " + gyro_unca1 + " gyro_unca2: " + gyro_unca2 +
                        " gyro_unca3: " + gyro_unca3 + " gyro_unca4: " + gyro_unca4 + " gyro_unca5: "
                        + gyro_unca5 + " gyro_unca6: " + gyro_unca6);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {


        }
    }

    private void insertpdrtodb(SQLiteDatabase db, double x, double y) {
        String sql = "insert into " + PrimeActivity.tableName +
                " ( PX , PY ) values (" + x + " , " + y + " );";
        db.execSQL(sql);


    }
}
