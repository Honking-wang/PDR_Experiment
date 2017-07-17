package com.example.wanghanqing.godbless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class ChooseActivity extends AppCompatActivity {

    private CheckBox locate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        locate = (CheckBox) findViewById(R.id.locatebox);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
