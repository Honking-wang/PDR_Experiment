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

import java.text.SimpleDateFormat;
import java.util.Date;


public class SensorActivity extends AppCompatActivity {

    private int step;
    private float[] accelerometerValues = new float[3];
    private float[] magneticfieldValues = new float[3];
    float[] r = new float[9];//旋转矩阵
    float[] values = new float[3];
    private float orient;

    private Button startButton;
    private Button stopButton;
    private TextView timTextView;
    private TextView oriTextView;
    private TextView steTextView;

    private SensorManager sensorManager = null;
    private Sensor sensor_DETECTOR = null;
    private Sensor sensor_ACCELEROMETER = null;
    private Sensor sensor_MAGNETIC = null;

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
        steTextView = (TextView) this.findViewById(R.id.step);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度传感器
        sensor_MAGNETIC = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensor_DETECTOR = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//
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
                stringBuilder.append(sdf.format(new Date()) + "   ");

                oriTextView.setText("" + orient);
                stringBuilder.append(orient + "   ");

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
            oriTextView.setText("" + orient);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }
    }

}



