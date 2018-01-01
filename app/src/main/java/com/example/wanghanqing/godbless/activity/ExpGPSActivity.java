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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.helper.MyDatabaseHelper;
import com.example.wanghanqing.godbless.utils.Utils;
import com.example.wanghanqing.godbless.view.AoGPSView;

import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;

public class ExpGPSActivity extends Activity {

    public static boolean FLAG;// 轨迹记录开关
    public static int COUNT;// 定位点个数
    public static double GX;
    public static double GY;

    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;
    public AoGPSView gaoView;
    public static WorkSpace gworkSpace;
    public static AoMap gaoMap;
    public static LocationManager glocationManager;
    public String provider2;
    public static Location glocation;
    public MyDatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    private Button gpsstart;
    private Button gpsback;
    Intent intent2;
    int expid;

    private SensorManager sensorManager = null;
    private Sensor sensor_DETECTOR = null;
    private MySensorEventListener mySensorEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expgps);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        gaoView = findViewById(R.id.aogpsview);
        gaoView.setActivity(this);
        gaoMap = new AoMap();
        gaoMap.openMap(GPJPATH);
        gaoView.setMap(gaoMap);//指定mapView显示map代表的地图文件

        gpsstart = findViewById(R.id.gpsstart);
        gpsback = findViewById(R.id.gpsback);
        gpsstart.setOnClickListener(new BCGJListener());
        gpsback.setOnClickListener(new GPSbackListener());

        //定位模块
        Toast.makeText(ExpGPSActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
        glocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider2 = glocationManager.getBestProvider(criteria, true);
        glocation = glocationManager.getLastKnownLocation(provider2);
        glocationManager.requestLocationUpdates(provider2, 1000, 10, locationListener);
        gworkSpace = gaoMap.getWorkSpace();

        //显示定位点和轨迹点
        COUNT = 0;
        updateWithNewLocation(glocation);


        //显示地图
        gaoView.resetView();
        gaoView.zoomView(-480, 800, 6);
        gaoView.updateView();
        //打开数据库
        dbHelper = new MyDatabaseHelper(this);
        sqLiteDatabase = dbHelper.getReadableDatabase();
        intent2 = getIntent();
        expid = Integer.parseInt(intent2.getStringExtra("expid"));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


    }

    final class BCGJListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mySensorEventListener = new MySensorEventListener();
            if (!FLAG) {
                // 如果轨迹功能开启。
                if (Utils.GPSisOPen(getApplicationContext())) {
                    if (glocation != null) {
                        FLAG = true;
                        COUNT = 0;
                        gaoView.updateView();
                        //insertgpstodb(GX, GY);
                        gpsstart.setText("结束试验");
                        sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR,
                                SensorManager.SENSOR_DELAY_FASTEST);
                        Toast.makeText(getApplicationContext(), "轨迹记录开始",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "卫星信号弱，无法获取位置信息。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请开启位置服务",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                FLAG = false;
                gpsstart.setText("开始GPS试验");
            }
        }
    }

    final class GPSbackListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ExpGPSActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    final class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    insertgpstodb(GX, GY);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    /**
     * 向数据库中添加GPS坐标点数据
     *
     * @param x
     * @param y
     */
    private void insertgpstodb(double x, double y) {
        String sql = "insert into EXP_table ( EXP_ID, GX , GY ) values (" + expid + "," + x + " , " + y + " );";
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

            WorkAreaParams areaparams = gaoMap.getWorkAreaParamsClone();
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
            GX = position[0];
            GY = position[1];

        } else {

            GX = 0;
            GY = 0;
            Toast.makeText(this, "无法获取坐标信息", Toast.LENGTH_LONG).show();
        }
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            glocation = location;
            updateWithNewLocation(glocation);
            gaoView.updateView();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
