package com.example.wanghanqing.godbless.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;

import com.example.wanghanqing.godbless.helper.FileHelper;
import com.example.wanghanqing.godbless.helper.MyDatabaseHelper;
import com.example.wanghanqing.godbless.utils.Utils;
import com.example.wanghanqing.godbless.values.Values;
import com.example.wanghanqing.godbless.view.AoPDRView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;

public class ExpPDRActivity extends Activity {

    public static double X;
    public static double Y;
    public static double PX;
    public static double PY;

    public static int count;
    public static int COUNT;
    public static boolean flag;
    public static boolean FLAG;

    public Button startpdr;
    public Button endpdr;
    public Button pdrback;
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
    private Sensor sensor_DETECTOR = null;
    private Sensor sensor_Gyroscope = null;
    private Sensor sensor_Gyroscope_uncalibrated = null;

    public StringBuilder stringBuilder;

    private MyOri1SensorEventListener myOri1SensorEventListener;
    private MySensorEventListener mySensorEventListener;

    public float orient0;
    public float orient;
    public float length;

    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;

    public AoPDRView pdraoMyView;
    public static WorkSpace pdrworkSpace;
    public static AoMap pdraoMap;
    public static LocationManager pdrlocationManager;
    public String pdrprovider;
    public static Location pdrlocation;

    public MyDatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    Intent intent2;
    int expid;

    private float torient;
    private float tgyro1;
    private float tgyro2;
    private float tgyro3;
    private float tgyro_unca1;
    private float tgyro_unca2;
    private float tgyro_unca3;
    private float tgyro_unca4;
    private float tgyro_unca5;
    private float tgyro_unca6;
    private float tacc1;
    private float tacc2;
    private float tacc3;
    private float tmag1;
    private float tmag2;
    private float tmag3;
    private int tstep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exppdr);
        //打开地图
        AoSysLib.loadLib(LIBPATH);
        pdraoMyView = findViewById(R.id.aopdrview);
        pdraoMyView.setActivity(this);
        pdraoMap = new AoMap();
        pdraoMap.openMap(GPJPATH);
        pdraoMyView.setMap(pdraoMap);
        //显示地图
        pdraoMyView.resetView();
        pdraoMyView.zoomView(-480, 800, 6);
        pdraoMyView.updateView();


        //打开数据库
        dbHelper = new MyDatabaseHelper(this);
        sqLiteDatabase = dbHelper.getReadableDatabase();
        intent2 = getIntent();
        expid = Integer.parseInt(intent2.getStringExtra("expid"));

        //定位模块
        Toast.makeText(ExpPDRActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
        pdrlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        pdrprovider = pdrlocationManager.getBestProvider(criteria, true);
        pdrlocation = pdrlocationManager.getLastKnownLocation(pdrprovider);
        pdrlocationManager.requestLocationUpdates(pdrprovider, 1000, 10, locationListener);
        pdrworkSpace = pdraoMap.getWorkSpace();

        intent = getIntent();
        lentype = Integer.parseInt(intent.getStringExtra("lentype"));
        oritype = Integer.parseInt(intent.getStringExtra("oritype"));
        startpdr = findViewById(R.id.startpdr);
        endpdr = findViewById(R.id.endpdr);
        pdrback = findViewById(R.id.pdrback);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_Gyroscope_uncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        stringBuilder = new StringBuilder();

        startpdr.setOnClickListener(new startpdrListener());
        endpdr.setOnClickListener(new endpdrListener());
        pdrback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpPDRActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * 实现了传感器数据的保存，频率28ms一次。
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            // 要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            try {
                handler.postDelayed(this, 20);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
                stringBuilder.append(sdf.format(new Date()) + "    ");

                stringBuilder.append("方向" + torient + "    ");

                stringBuilder.append("陀螺仪" + tgyro1 + "    " + tgyro2 + "    " + tgyro3 + "    ");

                stringBuilder.append("未校准陀螺仪" + tgyro_unca1 + "    " + tgyro_unca2 + "    " + tgyro_unca3 + "    "
                        + tgyro_unca4 + "    " + tgyro_unca5 + "    " + tgyro_unca6 + "    ");

                stringBuilder.append("加速度" + tacc1 + "    " + tacc2 + "    " + tacc3 + "    ");

                stringBuilder.append("磁力计" + tmag1 + "    " + tmag2 + "    " + tmag3 + "    ");

                stringBuilder.append("步数" + tstep + "\r\n");
            } catch (Exception e) {
                e.printStackTrace();// 12345678
            }
        }
    };


    final class startpdrListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            startpdr.setEnabled(false);
            endpdr.setEnabled(true);
            getorient();
            length = getlength();
            PX = X;
            PY = Y;
            mySensorEventListener = new MySensorEventListener();
            if (!FLAG) {
                if (Utils.GPSisOPen(getApplicationContext())) {
                    if (pdrlocation != null) {
                        FLAG = true;
                        COUNT = 0;
                        pdraoMyView.updateView();
                        Toast.makeText(getApplicationContext(), "轨迹记录开始", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "卫星信号弱，无法获取位置信息。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请开启位置服务", Toast.LENGTH_SHORT).show();
                }
            } else {
                FLAG = false;
            }
            if (!flag) {
                flag = true;
                count = 0;
                sensorManager.registerListener(mySensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope_uncalibrated, sensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR, SensorManager.SENSOR_DELAY_FASTEST);
                handler.postDelayed(runnable, 20);
            } else {

                sensorManager.unregisterListener(mySensorEventListener, sensor_ACCELEROMETER);
                sensorManager.unregisterListener(mySensorEventListener, sensor_MAGNETIC);
                sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope);
                sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope_uncalibrated);
                sensorManager.unregisterListener(mySensorEventListener, sensor_DETECTOR);
            }
        }
    }

    final class endpdrListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            startpdr.setEnabled(true);
            endpdr.setEnabled(false);
            handler.removeCallbacks(runnable);
            FileHelper helper = new FileHelper(
                    getApplicationContext());
            String NAME = "试验" + expid + ".txt";
            Values.saveSensor(Values.SENSORPATH, NAME,
                    stringBuilder.toString());
            Toast.makeText(getApplicationContext(),
                    NAME + "已保存", Toast.LENGTH_SHORT).show();

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
        float len = 0;
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

    final class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    tstep += 1;
                    orient = (float) (orient0 * Math.PI / 180);
                    PX = PX + length * Math.cos((5 * Math.PI / 2) - orient);
                    PY = PY + length * Math.sin((5 * Math.PI / 2) - orient);
                    pdraoMyView.updateView();
                    insertpdrtodb(X, Y, PX, PY);

                }
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                tgyro1 = sensorEvent.values[0];
                tgyro2 = sensorEvent.values[1];
                tgyro3 = sensorEvent.values[2];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                tgyro_unca1 = sensorEvent.values[0];
                tgyro_unca2 = sensorEvent.values[1];
                tgyro_unca3 = sensorEvent.values[2];
                tgyro_unca4 = sensorEvent.values[3];
                tgyro_unca5 = sensorEvent.values[4];
                tgyro_unca6 = sensorEvent.values[5];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                tacc1 = sensorEvent.values[0];
                tacc2 = sensorEvent.values[1];
                tacc3 = sensorEvent.values[2];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                tmag1 = sensorEvent.values[0];
                tmag2 = sensorEvent.values[1];
                tmag3 = sensorEvent.values[2];
            }
            torient = orient0;

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
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
            orient0 = (float) Math.toDegrees(values[0]);
            if (orient0 < 0) {
                orient0 += 360;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {


        }
    }


    private void insertpdrtodb(double x, double y, double px, double py) {
        String PRDid = "" + oritype + "" + lentype;
        String sql = "insert into EXP_table ( EXP_ID, GX , GY , PX , PY ,PRDid ) values " +
                "(" + expid + "," + x + " , " + y + "," + px + " , " + py + " , " + PRDid + " );";
        sqLiteDatabase.execSQL(sql);
    }

    /**
     * 经纬度转化为北京54坐标系，在地图上显示点必经的一步
     *
     * @param location
     */
    private void updateWithNewLocation(Location location) {
        double[] position = null;
        if (location != null) {
            double lat = location.getLatitude();// 纬度
            double lng = location.getLongitude();// 经度
            double x = lng / 180 * Math.PI;
            double y = lat / 180 * Math.PI;

            WorkAreaParams areaparams = pdraoMap.getWorkAreaParamsClone();
            m_dRate = areaparams.rate; // 比例尺
            WorkAreaParams.LengthType lType = areaparams.getCoordType(); // 一般地图都是“MM”或“M”
            if ((lType == WorkAreaParams.LengthType.MM) || (lType == WorkAreaParams.LengthType.MilliMeter))
                m_iCoordUnitRate = 1000;
            else if (lType == WorkAreaParams.LengthType.CentiMeter)
                m_iCoordUnitRate = 100;
            else if (lType == WorkAreaParams.LengthType.DeciMeter)
                m_iCoordUnitRate = 10;
            else if (lType == WorkAreaParams.LengthType.Meter)
                m_iCoordUnitRate = 1;

            double z = 117.0000 / 180 * Math.PI;

            position = ProjectionHelper.Gauss(z, x, y,
                    areaparams.getEarthType());
            position[0] = position[0] + 39000000;
            X = position[0];
            Y = position[1];

        } else {

            X = 0;
            Y = 0;
            Toast.makeText(this, "无法获取坐标信息", Toast.LENGTH_LONG).show();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            pdrlocation = location;
            updateWithNewLocation(pdrlocation);
            pdraoMyView.updateView();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            updateWithNewLocation(null);

        }
    };
}






