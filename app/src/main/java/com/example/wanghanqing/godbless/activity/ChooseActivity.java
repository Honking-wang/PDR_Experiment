package com.example.wanghanqing.godbless.activity;


import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.wanghanqing.godbless.R;

public class ChooseActivity extends AppCompatActivity {

    private RadioGroup rglen;
    private RadioGroup rgori;
    private RadioButton len1;
    private RadioButton len2;
    private RadioButton len3;
    private RadioButton ori1;
    public int lentype = 0;
    public int oritype = 0;

    private Button confirm;

    Intent intent2;
    int expid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        rglen = (RadioGroup) findViewById(R.id.rglen);
        rgori = (RadioGroup) findViewById(R.id.rgori);
        len1 = (RadioButton) findViewById(R.id.len1);
        len2 = (RadioButton) findViewById(R.id.len2);
        len3 = (RadioButton) findViewById(R.id.len3);
        ori1 = (RadioButton) findViewById(R.id.ori1);
        confirm = (Button) findViewById(R.id.btn_confirm);

        intent2 = getIntent();
        expid = Integer.parseInt(intent2.getStringExtra("expid"));

        rglen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == len1.getId()) {
                    lentype = 1;
                } else if (i == len2.getId()) {
                    lentype = 2;
                } else if (i == len3.getId()) {
                    lentype = 3;
                }
            }
        });

        rgori.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == ori1.getId()) {
                    oritype = 1;
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lentype == 0 && oritype != 0) {
                    Toast.makeText(ChooseActivity.this, "请选择步长估计模型", Toast.LENGTH_SHORT).show();
                } else if (lentype != 0 && oritype == 0) {
                    Toast.makeText(ChooseActivity.this, "请选择方向获取方式", Toast.LENGTH_SHORT).show();
                } else if (lentype == 0 && oritype == 0) {
                    Toast.makeText(ChooseActivity.this, "请选择两个参数的获取方式", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ChooseActivity.this, ExpPDRActivity.class);
                    intent.putExtra("lentype", lentype+"");
                    intent.putExtra("oritype", oritype+"");
                    intent.putExtra("expid", expid+"");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lentype = 0;
        oritype = 0;
    }
}
