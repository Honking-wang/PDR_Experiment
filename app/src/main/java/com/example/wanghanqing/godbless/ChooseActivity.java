package com.example.wanghanqing.godbless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class ChooseActivity extends AppCompatActivity {

    private boolean locflag;
    private boolean senflag;

    private CheckBox locate;
    private CheckBox sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        locate = (CheckBox) findViewById(R.id.locatebox);
        sensor = (CheckBox) findViewById(R.id.sensorbox);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!locflag) {

                    Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
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


    }
}
