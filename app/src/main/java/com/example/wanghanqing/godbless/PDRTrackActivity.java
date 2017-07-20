package com.example.wanghanqing.godbless;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.render.AoSysLib;

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class PDRTrackActivity extends Activity {


  // public static double[] e;

    public float orient;

    public float step;
    public float stepold;

    public float average;


    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;


    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];


    public AoPDRView mapView3;
    public static WorkSpace workSpace2;
    public static AoMap aoMap3;
    public static LocationManager locationManager2;
    public String provider2;
    public static Location location2;

    public SensorManager sensorManager;
    public Sensor sensorDet;
    public Sensor sensorAcc;
    public Sensor sensorMag;
    public MyPDRSensorEventListener myPDRSensorEventListener;


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
        setContentView(R.layout.activity_pdrtrack);


        //打开地图
        AoSysLib.loadLib(LIBPATH);
        mapView3 = findViewById(R.id.main3_view);
        mapView3.setActivity(this);
        aoMap3 = new AoMap();
        aoMap3.openMap(GPJPATH);


        mapView3.setMap(aoMap3);//指定mapView显示map代表的地图文件


        //显示地图
        mapView3.resetView();
        mapView3.zoomView(-480, 800, 6);
        mapView3.updateView();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorDet = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        myPDRSensorEventListener = new MyPDRSensorEventListener();



        mapView3.updateView();

        getStep(ChooseActivity.jibuqi);
        getOrient();

    }


    public void getStep(int i) {
        if (i == 1) {
            sensorManager.registerListener(myPDRSensorEventListener, sensorDet, SensorManager.SENSOR_DELAY_FASTEST);

        } else if (i == 2) {
            sensorManager.registerListener(myPDRSensorEventListener, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
        }

    }

    public void getOrient() {
        sensorManager.registerListener(myPDRSensorEventListener, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(myPDRSensorEventListener, sensorMag, SensorManager.SENSOR_DELAY_FASTEST);
    }


    class MyPDRSensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    step++;
                }
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();
                average = (float) Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2)
                        + Math.pow(sensorEvent.values[2], 2));
                detectorNewStep(average);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticfieldValues = sensorEvent.values.clone();
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


            if (step != stepold) {
                mapView3.addPoint(orient);
                mapView3.updateView();
                stepold = step;
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
                        step++;
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


}



