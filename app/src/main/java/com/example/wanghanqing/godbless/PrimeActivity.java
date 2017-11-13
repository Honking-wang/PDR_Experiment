package com.example.wanghanqing.godbless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PrimeActivity extends AppCompatActivity {
    public Button cjdb;
    public Button xzdb;
    public Button PDRexp;
    public Button GPSTra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);

        cjdb= (Button) findViewById(R.id.cjdb);
        xzdb= (Button) findViewById(R.id.xzdb);
        PDRexp= (Button) findViewById(R.id.PDRexp);
        GPSTra= (Button) findViewById(R.id.GPSTra);

        PDRexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrimeActivity.this, ChooseActivity.class);
                startActivity(intent);
            }
        });

        GPSTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrimeActivity.this, GPSTrackActivity.class);
                startActivity(intent);
            }
        });

        cjdb.setOnClickListener(new cjdbListener());
    }

    final class cjdbListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

        }
    }
}
