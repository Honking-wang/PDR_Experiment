package com.example.wanghanqing.godbless;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.AoGIS.database.AoMap;
import com.AoGIS.database.WorkAreaParams;
import com.AoGIS.database.WorkSpace;
import com.AoGIS.location.ProjectionHelper;
import com.AoGIS.render.AoSysLib;

import static com.example.wanghanqing.godbless.Values.GPJPATH;
import static com.example.wanghanqing.godbless.Values.LIBPATH;

public class PDRTrackActivity extends Activity {

    public static double XX;
    public static double YY;


    double m_dRate = 1;//比例尺
    int m_iCoordUnitRate;


    public AoPDRView mapView3;
    public static WorkSpace workSpace2;
    public static AoMap aoMap3;
    public static LocationManager locationManager2;
    public String provider2;
    public static Location location2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdrtrack);


        //打开地图
        AoSysLib.loadLib(LIBPATH);
        mapView3 = findViewById(R.id.main3_view);
        mapView3.setActivity(this);
        aoMap3 = new AoMap();
        aoMap3.openMap(GPJPATH);


        mapView3.setMap(aoMap3);//指定mapView显示map代表的地图文件


        //显示地图
        mapView3.resetView();
        mapView3.zoomView(-480, 800, 6);
        mapView3.updateView();

        //记录gps轨迹
        mapView3.x = XX;
        mapView3.y = YY;

        mapView3.updateView();
    }

    private void updateWithNewLocation(Location location) {
        double[] position = null;
        if (location != null) {
            double lat = location.getLatitude();// 纬度
            double lng = location.getLongitude();// 经度
            double x = lng / 180 * Math.PI;
            double y = lat / 180 * Math.PI;

            WorkAreaParams areaparams = aoMap3.getWorkAreaParamsClone();
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
}



