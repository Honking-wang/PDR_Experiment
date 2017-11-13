package com.example.wanghanqing.godbless;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.AoGIS.database.AoMap;
import com.AoGIS.render.AoSysLib;


import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class DeadReackoningActivity extends AppCompatActivity {


    public static int count;
    public static double X = 39443601;
    public static double Y = 4429854;
    public AoPDRView drView;
    public static AoMap aodrMap;
    public Button pdrstart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deadreackoning);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        drView = (AoPDRView) findViewById(R.id.dr_mapview);

        drView.setActivity(this);
        aodrMap = new AoMap();
        aodrMap.openMap(GPJPATH);
        drView.setMap(aodrMap);//指定mapView显示map代表的地图文件

        //显示地图
        drView.resetView();
        drView.zoomView(-480, 800, 6);
        drView.updateView();
        pdrstart = (Button) findViewById(R.id.BDstart);


        pdrstart.setOnClickListener(new PDRstartListener());


    }

    final class PDRstartListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(DeadReackoningActivity.this);
            dialog.setTitle("请输入轨迹信息");
            LayoutInflater factory = LayoutInflater.from(view.getContext());
            final View dialogView = factory.inflate(R.layout.dialog_sensorsave,
                    null);
            dialog.setView(dialogView);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText editText = (EditText) dialogView
                            .findViewById(R.id.message);// 保存info的Edittext
                    String NAME = editText.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("data", NAME);
                    intent.setClass(DeadReackoningActivity.this, DeadReackoning2Activity.class);
                    startActivity(intent);
                }
            });
            dialog.show();

        }
    }
}
