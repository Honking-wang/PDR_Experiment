package com.example.wanghanqing.godbless.activity;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.dialog.DescripDialog;
import com.example.wanghanqing.godbless.helper.MyDatabaseHelper;
import com.example.wanghanqing.godbless.utils.Utils;
import com.example.wanghanqing.godbless.view.AoMyView;


import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;


public class MainActivity extends AppCompatActivity {

    public static int COUNT;// 定位点个数
    public static boolean FLAG;// 轨迹记录开关
    public static double X;
    public static double Y;

    //显示地图相关的定义声明
    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;
    public AoMyView aoMyView;
    public static WorkSpace gworkSpace;
    public static AoMap aoMap;
    public static LocationManager locationManager;
    public String provider;
    public static Location location;

    public MyDatabaseHelper dbHelper;

    //按钮
    public Button description;
    public Button exp_GPS;
    public Button exp_PDR;

    DescripDialog descripDialog;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this);
        sqLiteDatabase = dbHelper.getReadableDatabase();

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        aoMyView = (AoMyView) findViewById(R.id.aomyview);
        aoMyView.setActivity(this);
        aoMap = new AoMap();
        aoMap.openMap(GPJPATH);
        aoMyView.setMap(aoMap);//指定mapView显示map代表的地图文件

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
        locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
        gworkSpace = aoMap.getWorkSpace();

        //显示定位点和轨迹点
        COUNT = 0;
        updateWithNewLocation(location);

        //显示地图
        aoMyView.resetView();
        aoMyView.zoomView(-480, 800, 6);
        aoMyView.updateView();

        description = (Button) findViewById(R.id.description);
        exp_GPS = (Button) findViewById(R.id.EXP_GPS);
        exp_PDR= (Button) findViewById(R.id.EXP_PDR);
        description.setOnClickListener(new desListener());
        exp_GPS.setOnClickListener(new GPSListener());
        exp_PDR.setOnClickListener(new PDRListener());


    }

    final class desListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showEditDialog(view);
        }

        public void showEditDialog(View view) {
            descripDialog = new DescripDialog(MainActivity.this, R.style.loading_dialog, onClickListener);
            descripDialog.show();
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tester = descripDialog.text_tester.getText().toString().trim();
                String facility = descripDialog.text_facility.getText().toString().trim();
                String expression = descripDialog.text_expression.getText().toString().trim();
                //向数据库插入数据
                insertData(sqLiteDatabase, tester, facility, expression);
                Toast.makeText(MainActivity.this, "添加信息成功", Toast.LENGTH_SHORT).show();
                descripDialog.dismiss();
            }
        };


    }

    final class GPSListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!FLAG) {
                // 如果轨迹功能开启。
                if (Utils.GPSisOPen(getApplicationContext())) {
                    if (location != null) {
                        FLAG = true;
                        COUNT = 0;
                        aoMyView.updateView();
                        insertgpstodb(X, Y);
                        exp_GPS.setText("结束GPS试验");
                        Toast.makeText(getApplicationContext(), "轨迹记录开始",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "卫星信号弱，无法获取位置信息。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请开启位置服务",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                exp_GPS.setText("GPR试验");
            }
        }
    }

    final class PDRListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
            startActivity(intent);
        }
    }


    /**
     * 添加数据文件
     *
     * @param sqLiteDatabase
     * @param tester
     * @param facility
     * @param expression
     */
    public void insertData(SQLiteDatabase sqLiteDatabase, String tester, String facility, String expression) {
        sqLiteDatabase.execSQL("insert into EXP_lab_table values( null , ? , ? , ?)"
                , new String[]{tester, facility, expression});
    }

    private void insertgpstodb(double x, double y) {
        String sql = "insert into EXP_table ( GX , GY ) values (" + x + " , " + y + " );";
        sqLiteDatabase.execSQL(sql);
    }


    /**
     * 经纬度转化为北京54坐标系，在地图上显示点必经的一步
     *
     * @param location
     */
    private void updateWithNewLocation(Location location) {
        double[] position = null;
        if (location != null) {
            double lat = location.getLatitude();// 纬度
            double lng = location.getLongitude();// 经度
            double x = lng / 180 * Math.PI;
            double y = lat / 180 * Math.PI;

            WorkAreaParams areaparams = aoMap.getWorkAreaParamsClone();
            m_dRate = areaparams.rate; // 比例尺
            WorkAreaParams.LengthType lType = areaparams.getCoordType(); // 一般地图都是“MM”或“M”
            if ((lType == WorkAreaParams.LengthType.MM) || (lType == WorkAreaParams.LengthType.MilliMeter))
                m_iCoordUnitRate = 1000;
            else if (lType == WorkAreaParams.LengthType.CentiMeter)
                m_iCoordUnitRate = 100;
            else if (lType == WorkAreaParams.LengthType.DeciMeter)
                m_iCoordUnitRate = 10;
            else if (lType == WorkAreaParams.LengthType.Meter)
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
            Toast.makeText(this, "无法获取坐标信息", Toast.LENGTH_LONG).show();
        }
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            MainActivity.location = location;
            updateWithNewLocation(location);
            aoMyView.updateView();

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


    @Override
    protected void onDestroy() {
        AoSysLib.freeLib();//释放系统库
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }

    }
}
