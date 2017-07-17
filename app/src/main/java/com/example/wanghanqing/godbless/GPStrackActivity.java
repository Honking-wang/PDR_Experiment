package com.example.wanghanqing.godbless;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.geometry.GeoClassType;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class GPStrackActivity extends AppCompatActivity {

    public static double X;
    public static double Y;
    public static float orient;

    public static float mDetector = 0;
    public static float mDetectorOld = 0;
    public static boolean stepflag;

    public int locflag = 0;
    public int peakflag = 0;
    public int detectflag = 0;

    public static boolean gpstrackflag = false;
    public static boolean pdrtrackflag = false;
    public static int gpsCOUNT;

    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];

    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;

    SensorManager sensorManager = null;

    public AoMyView mapView;
    public static WorkSpace workSpace;
    public static AoMap aoMap;
    public static LocationManager locationManager;
    public String provider;
    public static Location location;

    private Button track;
    private Button GPStrack;
    private Button PDRtrack;
    private Button pedometer;
    private Button detectpeak;
    private Button detectsensor;
    private Button record;
    private boolean isVisable = true;
    private boolean isVisable1 = true;
    private TextView detectpeak_text;
    private TextView locatetype_text;
    public static TextView sensordetect_text;
    public TextView newori;
    public TextView sensorCount_text;
    public Sensor sensorDetector;
    public Sensor sensorAcc;
    public Sensor sensorOri;
    public Sensor sensorMag;
    public com.example.wanghanqing.godbless.MainActivity.MySensorEventListenor mySensorEventListenor;
    public com.example.wanghanqing.godbless.MainActivity.MyOriSensorEventListener myOriSensorEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstrack);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        mapView = (AoMyView) findViewById(R.id.main_view);
        mapView.setActivity(this);
        aoMap = new AoMap();
        aoMap.openMap(GPJPATH);


        mapView.setMap(aoMap);//指定mapView显示map代表的地图文件

        //定位模块
        Toast.makeText(com.example.wanghanqing.godbless.MainActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 500, 3, locationListener);

        GeoClassType geoClassType[] = new GeoClassType[1];
        geoClassType[0] = GeoClassType.POLYGON;
        workSpace = aoMap.getWorkSpace();

        updateWithNewLocation(location);

        locatetype_text = (TextView) findViewById(R.id.locatetype);
        StringBuilder locString = new StringBuilder();
        locString.append("定位模式：");
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS卫星定位");
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locString.append("网络定位");
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS和网络定位");
        } else {
            locString.append("其他的定位方式");
        }
        locatetype_text.setText(locString);


        //显示地图
        mapView.resetView();
        mapView.zoomView(-480, 800, 6);
        mapView.updateView();


        //各种view的捆绑
        track = (Button) findViewById(R.id.track);
        GPStrack = (Button) findViewById(R.id.GPStrack);
        PDRtrack = (Button) findViewById(R.id.PDRtrack);
        pedometer = (Button) findViewById(R.id.pedometer);
        detectpeak = (Button) findViewById(R.id.peak);
        detectsensor = (Button) findViewById(R.id.detectsensor);
        record = (Button) findViewById(R.id.record);
        sensordetect_text = (TextView) findViewById(R.id.sensordetect);
        detectpeak_text = (TextView) findViewById(R.id.detectpeak);
        sensorCount_text = (TextView) findViewById(R.id.sensorcount);
        newori = (TextView) findViewById(R.id.newori);











        //Gps轨迹功能
        GPStrack.setOnClickListener(new com.example.wanghanqing.godbless.MainActivity.GPSListener());


    }


}


class MyOriSensorEventListener implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            sensorCount_text.setText("old与正北夹角" + sensorEvent.values[0]);
        }
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
        newori.setText("new与正北夹角" + orient);

        while (mDetector != mDetectorOld) {
            mDetectorOld = mDetector;
            mapView.autoAddPoint(50.0f, orient);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

    private void updateWithNewLocation(Location location) {
        double[] position = null;
        if (location != null) {
            double lat = location.getLatitude();// 纬度
            double lng = location.getLongitude();// 经度
            double x = lng / 180 * Math.PI;
            double y = lat / 180 * Math.PI;

            WorkAreaParams areaparams = aoMap.getWorkAreaParamsClone();
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
            Toast.makeText(this, "无法获取坐标信息或关闭定位功能", Toast.LENGTH_LONG).show();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            com.example.wanghanqing.godbless.MainActivity.location = location;
            updateWithNewLocation(location);
            mapView.updateView();

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

final class GPSListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {

        if (!gpstrackflag) {
            if (locflag == 1 && location == null) {
                Toast.makeText(com.example.wanghanqing.godbless.MainActivity.this, "卫星信号弱，请稍等", Toast.LENGTH_SHORT).show();
            } else if (locflag != 1 && location == null) {
                Toast.makeText(com.example.wanghanqing.godbless.MainActivity.this, "请开启定位功能", Toast.LENGTH_SHORT).show();
            } else {
                gpstrackflag = true;
                gpsCOUNT = 0;
                mapView.updateView();
                GPStrack.setText("轨迹关闭");
                Toast.makeText(getApplicationContext(), "轨迹记录开始",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            double[] points = new double[2 * (gpsCOUNT + 1)];// 加入了当前位置，所以+1，COUNT不包括当前位置
            double[] zuobiao;
            for (int i = 0; i < gpsCOUNT; i++) {
                zuobiao = mapView.List.get(i);
                points[i * 2] = zuobiao[0];
                points[i * 2 + 1] = zuobiao[1];
            }
            // 加入当前点
            points[2 * gpsCOUNT] = X;
            points[2 * gpsCOUNT + 1] = Y;

            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < gpsCOUNT; i++) {// 无需加入当前位置的点
                stringBuffer.append(points[2 * i] + " ");
                stringBuffer.append(points[2 * i + 1] + "\n");
            }
            Toast.makeText(com.example.wanghanqing.godbless.MainActivity.this, stringBuffer.toString(),
                    Toast.LENGTH_SHORT).show();

            gpstrackflag = false;
            gpsCOUNT = 0;
            GPStrack.setText("轨迹开启");
            mapView.updateView();

        }


    }

}


