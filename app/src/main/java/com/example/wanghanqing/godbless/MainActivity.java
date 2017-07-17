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
import com.AoGIS.database.WorkAreaParams.LengthType;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.geometry.GeoClassType;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;


import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class MainActivity extends AppCompatActivity {
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
    public MySensorEventListenor mySensorEventListenor;
    public MyOriSensorEventListener myOriSensorEventListener;


    /**
     * 加速度传感器记步的一些变量声明
     */
    public static float average;
    public static int STEP = 0;

    final int valueNum = 5;


    //用于存放计算阈值的波峰波谷差值
    float[] tempValue = new float[valueNum];
    int tempCount = 0;

    //当前传感器的值
    float gravityNew = 0;
    //上次传感器的值
    float gravityOld = 0;
    //此次波峰的时间
    long timeOfThisPeak = 0;
    //上次波峰的时间
    long timeOfLastPeak = 0;
    //当前的时间
    long timeOfNow = 0;
    //波峰值
    float peakOfWave = 0;
    //波谷值
    float valleyOfWave = 0;
    //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
    final float initialValue = (float) 1.7;
    //初始阈值
    float ThreadValue = (float) 2.0;

    //是否上升的标志位
    boolean isDirectionUp = false;
    //持续上升次数
    int continueUpCount = 0;
    //上一点的持续上升的次数，为了记录波峰的上升次数
    int continueUpFormerCount = 0;
    //上一点的状态，上升还是下降
    boolean lastStatus = false;

    //初始范围
    float minValue = 11f;
    float maxValue = 19.6f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        mapView = (AoMyView) findViewById(R.id.main_view);
        mapView.setActivity(this);
        aoMap = new AoMap();
        aoMap.openMap(GPJPATH);


        mapView.setMap(aoMap);//指定mapView显示map代表的地图文件

        //定位模块
        Toast.makeText(MainActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
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


        record.setOnClickListener(new RecordListener());


        //按钮隐藏与可见
        GPStrack.setVisibility(View.INVISIBLE);
        PDRtrack.setVisibility(View.INVISIBLE);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisable) {
                    GPStrack.setVisibility(View.VISIBLE);
                    PDRtrack.setVisibility(View.VISIBLE);
                    isVisable = false;
                } else {
                    GPStrack.setVisibility(View.INVISIBLE);
                    PDRtrack.setVisibility(View.INVISIBLE);
                    isVisable = true;
                }
            }
        });


        detectpeak.setVisibility(View.INVISIBLE);
        detectsensor.setVisibility(View.INVISIBLE);
        pedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisable1) {
                    detectpeak.setVisibility(View.VISIBLE);
                    detectsensor.setVisibility(View.VISIBLE);
                    isVisable1 = false;
                } else {
                    detectpeak.setVisibility(View.INVISIBLE);
                    detectsensor.setVisibility(View.INVISIBLE);
                    isVisable1 = true;
                }
            }
        });


        //获取传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorOri = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        mySensorEventListenor = new MySensorEventListenor();
        myOriSensorEventListener = new MyOriSensorEventListener();

        //定位按钮实现定位功能
//        locate.setOnClickListener(new LocateListener());

        //计步器波峰检测算法按钮实现计步器功能
        detectpeak.setOnClickListener(new DetectPeakListener());

        //计步传感器按钮实现几步功能
        detectsensor.setOnClickListener(new DetecSensorListener());

        //Gps轨迹功能
        GPStrack.setOnClickListener(new GPSListener());

        //PDR轨迹功能
        PDRtrack.setOnClickListener(new PDRListener());

    }

    class RecordListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), SensorActivity.class);
            startActivity(intent);
        }
    }

    class DetectPeakListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (peakflag) {

                case 0:
                    detectpeak_text.setText("波峰检测算法步数：");
                    Toast.makeText(MainActivity.this, "加速度传感器被激活", Toast.LENGTH_SHORT).show();
                    sensorManager.registerListener(mySensorEventListenor, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
                    peakflag = 1;
                    break;
                case 1:
                    sensorManager.unregisterListener(mySensorEventListenor, sensorAcc);
                    Toast.makeText(MainActivity.this, "加速度传感器被注销", Toast.LENGTH_SHORT).show();
                    peakflag = 0;
                    break;
            }
        }
    }

    class DetecSensorListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (detectflag) {

                case 0:
                    sensordetect_text.setText("detect传感器步数：");
                    Toast.makeText(MainActivity.this, "计步传感器被激活", Toast.LENGTH_SHORT).show();
                    sensorManager.registerListener(mySensorEventListenor, sensorDetector, SensorManager.SENSOR_DELAY_FASTEST);
                    detectflag = 1;
                    break;
                case 1:
                    sensorManager.unregisterListener(mySensorEventListenor, sensorDetector);
                    Toast.makeText(MainActivity.this, "计步传感器已经被注销", Toast.LENGTH_SHORT).show();
                    detectflag = 0;
                    break;
            }
        }
    }

    class MySensorEventListenor implements SensorEventListener {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    mDetector++;
                    sensordetect_text.setText("detect传感器步数：" + mDetector);
                }
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                average = (float) Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2)
                        + Math.pow(sensorEvent.values[2], 2));
                detectorNewStep(average);
                detectpeak_text.setText("波峰检测算法步数：" + STEP);
            }
        }

        public void detectorNewStep(float values) {
            //如果上一时刻传感器值为0，则把现在的值付给上一时刻（用来比较上升还是下降）
            if (gravityOld == 0) {
                gravityOld = values;
            } else {
                //通过这一时刻传感器的值和上一时刻传感器的值判断波峰波谷
                if (DetectorPeak(values, gravityOld)) {
                    timeOfLastPeak = timeOfThisPeak;//把这个波峰时间赋给上个波峰时间
                    timeOfNow = System.currentTimeMillis();//获取当前的时间
                    /**
                     * 当前时间和上一波峰时间差大于等于200毫秒，0.2秒
                     * 波峰值-波谷值大于等于2
                     * 当前时间和上一次波峰时间差小于2000毫秒，2秒（过滤停下、突然拿起手机等）
                     */
                    if (timeOfNow - timeOfLastPeak >= 200
                            && (peakOfWave - valleyOfWave >= ThreadValue) && (timeOfNow - timeOfLastPeak) <= 2000) {
                        timeOfThisPeak = timeOfNow;//把当前时间赋值给此刻波峰
                        //更新界面的处理，不涉及到算法
                        STEP++;
                    }
                    if (timeOfNow - timeOfLastPeak >= 200
                            && (peakOfWave - valleyOfWave >= initialValue)) {
                        timeOfThisPeak = timeOfNow;
                        ThreadValue = Peak_Valley_Thread(peakOfWave - valleyOfWave);
                    }
                }
            }
            gravityOld = values;//判断结束，把这一时刻传感器的值付给上一时刻
        }

        /*
         * 检测波峰
         * 以下四个条件判断为波峰：
         * 1.目前点为下降的趋势：isDirectionUp为false
         * 2.之前的点为上升的趋势：lastStatus为true
         * 3.到波峰为止，持续上升大于等于2次
         * 4.波峰值大于1.2g,小于2g
         * 记录波谷值
         * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
         * 2.所以要记录每次的波谷值，为了和下次的波峰做对比
         * */
        public boolean DetectorPeak(float newValue, float oldValue) {
            lastStatus = isDirectionUp;
            if (newValue >= oldValue) {
                isDirectionUp = true;
                continueUpCount++;
            } else {
                continueUpFormerCount = continueUpCount;
                continueUpCount = 0;
                isDirectionUp = false;
            }
            /**
             * 目前为下降状态
             * 上一状态为上升状态
             * 上升状态计数至少为两次
             * 波峰值在1.2g到2g之间
             */
            if (!isDirectionUp && lastStatus
                    && (continueUpFormerCount >= 2 && (oldValue >= minValue && oldValue < maxValue))) {
                peakOfWave = oldValue;
                return true;
            } else if (!lastStatus && isDirectionUp) {
                valleyOfWave = oldValue;
                return false;

            } else {
                return false;
            }
        }


        /*
         * 阈值的计算
         * 通过已经过的波峰波谷的差值动态的估计阈值，梯度化阈值
         * 1.通过波峰波谷的差值计算阈值
         * 2.记录4个值，存入tempValue[]数组中
         * 3.在将数组传入函数averageValue中计算阈值
         * */
        public float Peak_Valley_Thread(float value) {
            float tempThread = ThreadValue;
            if (tempCount < valueNum) {
                tempValue[tempCount] = value;
                tempCount++;
            } else {
                tempThread = averageValue(tempValue, valueNum);
                for (int i = 1; i < valueNum; i++) {
                    tempValue[i - 1] = tempValue[i];
                }
                tempValue[valueNum - 1] = value;
            }
            return tempThread;

        }


        public float averageValue(float value[], int n) {
            float ave = 0;
            for (int i = 0; i < n; i++) {
                ave += value[i];
            }
            ave = ave / valueNum;
            if (ave >= 8) {
                ave = (float) 4.3;
            } else if (ave >= 7 && ave < 8) {
                ave = (float) 3.3;
            } else if (ave >= 4 && ave < 7) {
                ave = (float) 2.3;
            } else if (ave >= 3 && ave < 4) {
                ave = (float) 2.0;
            } else {
                ave = (float) 1.7;
            }
            return ave;
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

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
            LengthType lType = areaparams.getCoordType(); // 一般地图都是“MM”或“M”
            if ((lType == LengthType.MM) || (lType == LengthType.MilliMeter))
                m_iCoordUnitRate = 1000;
            else if (lType == LengthType.CentiMeter)
                m_iCoordUnitRate = 100;
            else if (lType == LengthType.DeciMeter)
                m_iCoordUnitRate = 10;
            else if (lType == LengthType.Meter)
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
            MainActivity.location = location;
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
                    Toast.makeText(MainActivity.this, "卫星信号弱，请稍等", Toast.LENGTH_SHORT).show();
                } else if (locflag != 1 && location == null) {
                    Toast.makeText(MainActivity.this, "请开启定位功能", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, stringBuffer.toString(),
                        Toast.LENGTH_SHORT).show();

                gpstrackflag = false;
                gpsCOUNT = 0;
                GPStrack.setText("轨迹开启");
                mapView.updateView();

            }


        }

    }

    final class PDRListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!pdrtrackflag) {
                Toast.makeText(MainActivity.this, "请先开启计步器", Toast.LENGTH_SHORT).show();
                sensorManager.registerListener(myOriSensorEventListener, sensorOri, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(myOriSensorEventListener, sensorMag, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(myOriSensorEventListener, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
                mapView.mPDRx = MainActivity.X;
                mapView.mPDRy = MainActivity.Y;
                mapView.addPoint();
                mapView.updateView();
                pdrtrackflag = true;
            } else {
                sensorManager.unregisterListener(myOriSensorEventListener, sensorMag);
                sensorManager.unregisterListener(myOriSensorEventListener, sensorAcc);
                sensorManager.unregisterListener(myOriSensorEventListener, sensorOri);
                pdrtrackflag = false;
            }

        }
    }


    @Override
    protected void onDestroy() {
        AoSysLib.freeLib();//释放系统库
        super.onDestroy();

    }
}
