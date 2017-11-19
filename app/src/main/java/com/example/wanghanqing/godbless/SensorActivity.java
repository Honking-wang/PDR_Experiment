package com.example.wanghanqing.godbless;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanghanqing.godbless.values.Values;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SensorActivity extends AppCompatActivity {

    private int step;
    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];
    private float orient;
    private float orient2;
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

    private Button startButton;
    private Button stopButton;
    private TextView timTextView;
    private TextView oriTextView;
    private TextView ori2TextView;
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

    public StringBuilder stringBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        startButton = (Button) this.findViewById(R.id.start);
        startButton.setOnClickListener(new startListener());
        stopButton = (Button) this.findViewById(R.id.stop);
        stopButton.setOnClickListener(new stopListener());
        stopButton.setEnabled(false);
        timTextView = (TextView) this.findViewById(R.id.time);
        oriTextView = (TextView) this.findViewById(R.id.orient);
        ori2TextView = (TextView) findViewById(R.id.orient2);
        gyroTextView = (TextView) findViewById(R.id.gyro);
        gyro_uncaTextView = (TextView) findViewById(R.id.gyro2);
        accTextView = (TextView) findViewById(R.id.acc);
        magTextView = (TextView) findViewById(R.id.magn);
        steTextView = (TextView) this.findViewById(R.id.step);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度传感器
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor_Oritation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//已弃用，强行实验对比
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensor_Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_Gyroscope_uncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        mySensorEventListener = new MySensorEventListener();
        stringBuilder = new StringBuilder();

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
                timTextView.setText(sdf.format(new Date()));
                stringBuilder.append(sdf.format(new Date()) + "    ");

                oriTextView.setText("" + orient);
                stringBuilder.append(orient + "    ");

                ori2TextView.setText("" + orient2);
                stringBuilder.append(orient2 + "    ");

                gyroTextView.setText("" + gyro1 + "" + gyro2 + "" + gyro3);
                stringBuilder.append(gyro1 + "    " + gyro2 + "    " + gyro3 + "    ");

                gyro_uncaTextView.setText("" + gyro_unca1 + "" + gyro_unca2 + "" + gyro_unca3 + ""
                        + gyro_unca4 + "" + gyro_unca5 + "" + gyro_unca6 + "    ");

                stringBuilder.append(gyro_unca1 + "    " + gyro_unca2 + "    " + gyro_unca3 + "    "
                        + gyro_unca4 + "    " + gyro_unca5 + "    " + gyro_unca6 + "    ");

                accTextView.setText("" + acc1 + "" + acc2 + "" + acc3);
                stringBuilder.append(acc1 + "    " + acc2 + "    " + acc3 + "    ");

                magTextView.setText("" + mag1 + "" + mag2 + "" + mag3);
                stringBuilder.append(mag1 + "    " + mag2 + "    " + mag3 + "    ");


                steTextView.setText("" + step);
                stringBuilder.append(step + "\r\n");
            } catch (Exception e) {
                e.printStackTrace();// 12345678
            }
        }
    };


    final class startListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            sensorManager.registerListener(mySensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, sensor_MAGNETIC, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, sensor_DETECTOR, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, sensor_Oritation, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, sensor_Gyroscope_uncalibrated, sensorManager.SENSOR_DELAY_FASTEST);
            handler.postDelayed(runnable, 20);
        }
    }


    final class stopListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            handler.removeCallbacks(runnable);
            sensorManager.unregisterListener(mySensorEventListener, sensor_ACCELEROMETER);
            sensorManager.unregisterListener(mySensorEventListener, sensor_MAGNETIC);
            sensorManager.unregisterListener(mySensorEventListener, sensor_DETECTOR);
            sensorManager.unregisterListener(mySensorEventListener, sensor_Oritation);
            sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope);
            sensorManager.unregisterListener(mySensorEventListener, sensor_Gyroscope_uncalibrated);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    v.getContext());
            LayoutInflater factory = LayoutInflater.from(v.getContext());
            final View dialogView = factory.inflate(R.layout.dialog_sensorsave,
                    null);
            builder.setTitle("请输入轨迹线信息");
            builder.setView(dialogView);
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) dialogView
                                    .findViewById(R.id.message);// 保存info的Edittext
                            FileHelper helper = new FileHelper(
                                    getApplicationContext());
                            String NAME = editText.getText().toString()
                                    + ".txt";
                            Values.saveSensor(Values.SENSORPATH, NAME,
                                    stringBuilder.toString());
                            Toast.makeText(getApplicationContext(),
                                    NAME + "已保存", Toast.LENGTH_SHORT).show();
                        }

                    });
            builder.show();
        }
    }


    final class MySensorEventListener implements SensorEventListener {
        // 可以得到传感器实时测量出来的变化值

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (sensorEvent.values[0] == 1.0) {
                    step++;
                    steTextView.setText("" + step);
                }
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orient = sensorEvent.values[0];
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
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values.clone();
                acc1 = sensorEvent.values[0];
                acc2 = sensorEvent.values[1];
                acc3 = sensorEvent.values[2];
                accTextView.setText(" acc1: " + sensorEvent.values[0] + " acc2: " + sensorEvent.values[1] +
                        " acc3: " + sensorEvent.values[2]);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticfieldValues = sensorEvent.values.clone();
                mag1 = sensorEvent.values[0];
                mag2 = sensorEvent.values[1];
                mag3 = sensorEvent.values[2];
                magTextView.setText(" mag1: " + sensorEvent.values[0] + " mag2: " + sensorEvent.values[1] +
                        " mag3: " + sensorEvent.values[2]);
            }
            SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticfieldValues);
            SensorManager.getOrientation(r, values);
            orient2 = (float) Math.toDegrees(values[0]);
            if (orient2 < 0) {
                orient2 += 360;
            }
            oriTextView.setText("" + orient2);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }
    }

}



