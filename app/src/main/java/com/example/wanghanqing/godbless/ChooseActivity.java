package com.example.wanghanqing.godbless;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChooseActivity extends AppCompatActivity {

    private boolean locflag;
    private boolean senflag;
    private boolean gpstrackflag;
    private boolean pdrtrackflag;

    public static int jibuqi;
    public static int suanfa;

    private CheckBox locate;
    private CheckBox sensor;
    private CheckBox gpstrack;
    private CheckBox pdrtrack;

    private RadioGroup dx1;
    private RadioGroup dx2;
    private RadioButton jibu;
    private RadioButton bofeng;
    private RadioButton wu;
    private RadioButton ekf;

    private Button queren;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        locate = (CheckBox) findViewById(R.id.locatebox);
        sensor = (CheckBox) findViewById(R.id.sensorbox);
        gpstrack = (CheckBox) findViewById(R.id.GPS);
        pdrtrack = (CheckBox) findViewById(R.id.PDR);
        dx1 = (RadioGroup) findViewById(R.id.dx1);
        dx2 = (RadioGroup) findViewById(R.id.dx2);
        jibu = (RadioButton) findViewById(R.id.jibu);
        bofeng = (RadioButton) findViewById(R.id.bofeng);
        wu = (RadioButton) findViewById(R.id.wu);
        ekf = (RadioButton) findViewById(R.id.ekf);
        queren = (Button) findViewById(R.id.queren);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!locflag) {

                    Intent intent = new Intent(ChooseActivity.this, DeadReackoningActivity.class);
                    startActivity(intent);
                    locflag = true;
                } else {
                    locflag = false;
                }
            }
        });

        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!senflag) {
                    Intent intent = new Intent(ChooseActivity.this, SensorActivity.class);
                    startActivity(intent);
                    senflag = true;
                } else {
                    senflag = false;
                }
            }
        });

        gpstrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gpstrackflag) {
                    Intent intent = new Intent(ChooseActivity.this, GPSTrackActivity.class);
                    startActivity(intent);
                    gpstrackflag = true;
                } else {
                    gpstrackflag = false;
                }
            }
        });

        pdrtrack.setOnClickListener(new PDRClickListener());

        queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jibuqi == 0 || suanfa == 0) {
                    Toast.makeText(ChooseActivity.this, "请选择记步方式和算法", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(ChooseActivity.this, PDRTrackActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    final class PDRClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!pdrtrackflag) {
                Toast.makeText(ChooseActivity.this, "请选择记步方式及算法并确认", Toast.LENGTH_LONG).show();
                dx1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if (i == jibu.getId()) {
                            jibuqi = 1;
                        } else {
                            jibuqi = 2;
                        }
                    }
                });
                dx2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if (i == wu.getId()) {
                            suanfa = 1;
                        } else if (i == ekf.getId()) {
                            suanfa = 2;
                        }
                    }
                });
                pdrtrackflag = true;
            } else {
                pdrtrackflag = false;
            }
        }

    }


}
