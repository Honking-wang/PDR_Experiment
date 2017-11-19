package com.example.wanghanqing.godbless.activity;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;
import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.utils.Utils;
import com.example.wanghanqing.godbless.values.Values;
import com.example.wanghanqing.godbless.view.AoGPSView;

import static com.example.wanghanqing.godbless.values.Values.GPJPATH;
import static com.example.wanghanqing.godbless.values.Values.LIBPATH;

public class GPSTrackActivity extends Activity {

    public static boolean FLAG;// 轨迹记录开关
    public static int COUNT;// 定位点个数
    public static double GX;
    public static double GY;

    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;
    public AoGPSView gaoView;
    public static WorkSpace gworkSpace;
    public static AoMap gaoMap;
    public static LocationManager glocationManager;
    public String provider2;
    public static Location glocation;
    public SQLiteDatabase dbGPS;
    private TextView locatetype_text;
    private Button bcgj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstrack);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        gaoView = findViewById(R.id.gaoview);
        gaoView.setActivity(this);
        gaoMap = new AoMap();
        gaoMap.openMap(GPJPATH);
        gaoView.setMap(gaoMap);//指定mapView显示map代表的地图文件

        bcgj = findViewById(R.id.bcgj);
        bcgj.setOnClickListener(new BCGJListener());

        //定位模块
        Toast.makeText(GPSTrackActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
        glocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider2 = glocationManager.getBestProvider(criteria, true);
        glocation = glocationManager.getLastKnownLocation(provider2);
        glocationManager.requestLocationUpdates(provider2, 1000, 10, locationListener);
        gworkSpace = gaoMap.getWorkSpace();

        //显示定位点和轨迹点
        COUNT = 0;
        updateWithNewLocation(glocation);

        //显示定位模式
        locatetype_text = findViewById(R.id.locatetype2);
        StringBuilder locString = new StringBuilder();
        locString.append("定位模式：");
        if (glocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !glocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS卫星定位");
        } else if (glocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !glocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locString.append("网络定位");
        } else if (glocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && glocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS和网络定位");
        } else {
            locString.append("其他的定位方式");
        }
        locatetype_text.setText(locString);


        //显示地图
        gaoView.resetView();
        gaoView.zoomView(-480, 800, 6);
        gaoView.updateView();
        //打开数据库
        dbGPS = SQLiteDatabase.openOrCreateDatabase(Values.SENSORPATH + "GPSData.db3", null);


    }

    final class BCGJListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!FLAG) {
                // 如果轨迹功能开启。
                if (Utils.GPSisOPen(getApplicationContext())) {
                    if (glocation != null) {
                        FLAG = true;
                        COUNT = 0;
                        gaoView.updateView();
                        insertgpstodb(dbGPS, GX, GY);
                        bcgj.setText("轨迹关闭");
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
            }
        }
    }

    /**
     * 向数据库中添加GPS坐标点数据
     *
     * @param db
     * @param x
     * @param y
     */
    private void insertgpstodb(SQLiteDatabase db, double x, double y) {
        String sql = "insert into " + PrimeActivity.tableName +
                " ( GX , GY ) values (" + x + " , " + y + " );";
        db.execSQL(sql);
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

            WorkAreaParams areaparams = gaoMap.getWorkAreaParamsClone();
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
            GX = position[0];
            GY = position[1];

        } else {

            GX = 0;
            GY = 0;
            Toast.makeText(this, "无法获取坐标信息", Toast.LENGTH_LONG).show();
        }
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            glocation = location;
            updateWithNewLocation(glocation);
            gaoView.updateView();

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
    public void onDestroy() {
        super.onDestroy();
        if (dbGPS != null && dbGPS.isOpen()) {
            dbGPS.close();
        }
    }
}
