package com.example.wanghanqing.godbless;

import android.app.Activity;
import android.content.Context;
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
import com.AoGIS.geometry.GeoClassType;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class GPSTrackActivity extends Activity {

    //public static boolean DISPLAY;// 轨迹显示开关
    public static java.util.ArrayList<double[]> displayList;// 读取的轨迹坐标记录
    public static boolean FLAG;// 轨迹记录开关
    public static int COUNT;// 定位点个数

    public static double XX;
    public static double YY;




    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;


    public AoGPSView mapView2;
    public static WorkSpace workSpace2;
    public static AoMap aoMap2;
    public static LocationManager locationManager2;
    public String provider2;
    public static Location location2;


    private TextView locatetype_text2;
    private Button bcgj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstrack);

        //打开地图
        AoSysLib.loadLib(LIBPATH);
        mapView2 = findViewById(R.id.main2_view);
        mapView2.setActivity(this);
        aoMap2 = new AoMap();
        aoMap2.openMap(GPJPATH);
        mapView2.setMap(aoMap2);//指定mapView显示map代表的地图文件

        bcgj = (Button)findViewById(R.id.bcgj);
        bcgj.setOnClickListener(new BCGJListener());

        //定位模块
        Toast.makeText(GPSTrackActivity.this, "正在进行定位", Toast.LENGTH_SHORT).show();
        locationManager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider2 = locationManager2.getBestProvider(criteria, true);
        location2 = locationManager2.getLastKnownLocation(provider2);
        locationManager2.requestLocationUpdates(provider2, 1000, 10, locationListener);


        //int ids[] = new int[1];
        //GeoClassType geoClassType[] = new GeoClassType[1];

        //ids[0] = 1;
        //geoClassType[0] = GeoClassType.POLYGON;
        workSpace2 = aoMap2.getWorkSpace();




        COUNT = 0;
        updateWithNewLocation(location2);

        locatetype_text2 = findViewById(R.id.locatetype2);
        StringBuilder locString = new StringBuilder();
        locString.append("定位模式：");
        if (locationManager2.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager2.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS卫星定位");
        } else if (locationManager2.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !locationManager2.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locString.append("网络定位");
        } else if (locationManager2.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager2.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locString.append("GPS和网络定位");
        } else {
            locString.append("其他的定位方式");
        }
        locatetype_text2.setText(locString);


        //显示地图
        mapView2.resetView();
        mapView2.zoomView(-480, 800, 6);
        mapView2.updateView();



    }




    final class BCGJListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!FLAG) {
                // 如果轨迹功能开启。
                if (Utils.GPSisOPen(getApplicationContext())) {
                    if (location2 != null) {
                        FLAG = true;
                        COUNT = 0;
                        mapView2.updateView();
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
            } else {

                double[] points = new double[2 * (COUNT + 1)];// 加入了当前位置，所以+1，COUNT不包括当前位置
                double[] zuobiao;
                for (int i = 0; i < COUNT; i++) {
                    zuobiao = mapView2.List.get(i);
                    points[i * 2] = zuobiao[0];
                    points[i * 2 + 1] = zuobiao[1];
                }
                // 加入当前点
                points[2 * COUNT] = XX;
                points[2 * COUNT + 1] = YY;

                StringBuffer stringBuffer = new StringBuffer();

                for (int i = 0; i < COUNT; i++) {// 无需加入当前位置的点
                    stringBuffer.append(points[2 * i] + " ");
                    stringBuffer.append(points[2 * i + 1] + "\n");
                }

                FLAG = false;
                COUNT = 0;
                bcgj.setText("轨迹开启");
                mapView2.updateView();

            }

        }
    }









    private void updateWithNewLocation(Location location) {
        double[] position = null;
        if (location != null) {
            double lat = location.getLatitude();// 纬度
            double lng = location.getLongitude();// 经度
            double x = lng / 180 * Math.PI;
            double y = lat / 180 * Math.PI;

            WorkAreaParams areaparams = aoMap2.getWorkAreaParamsClone();
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
            XX = position[0];
            YY = position[1];

        } else {

            XX = 0;
            YY = 0;
            Toast.makeText(this, "无法获取坐标信息", Toast.LENGTH_LONG).show();
        }
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            location2 = location;
            updateWithNewLocation(location2);
            mapView2.updateView();

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
}
