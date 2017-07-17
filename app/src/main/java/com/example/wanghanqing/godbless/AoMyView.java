package com.example.wanghanqing.godbless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.AoGIS.render.AoView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wanghanqing on 2017/6/21.
 */

public class AoMyView extends AoView {


    private double[] preCoord;
    private Bitmap dingweiBitmap;
    private Bitmap guijidianBitmap;
    private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    private Canvas canvas;
    private float[] arr;// 用于坐标转换的x、y坐标值
    private int wx;// 坐标转换后的x
    private int wy;// 坐标转换后的y


    public static ArrayList<double[]> PDRList;
    public double mPDRx;
    public double mPDRy;
    private int wPDRx;// 坐标转换后的x
    private int wPDRy;// 坐标转换后的y

    public AoMyView(Context context) {
        super(context);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new ArrayList<double[]>();
        PDRList = new ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoMyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        PDRList = new ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoMyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        PDRList = new ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    android.graphics.Paint paint = new android.graphics.Paint();

    public void addPoint() {
        PDRList.add(new double[]{mPDRx, mPDRy});
    }

    public void autoAddPoint(float stepLen, float orient) {
        mPDRx += stepLen * Math.sin(Math.toRadians(orient));
        mPDRy += stepLen * Math.cos(Math.toRadians(orient));
        PDRList.add(new double[]{mPDRx, mPDRy});
    }

    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);
        // 进行轨迹点绘制
        if (!MainActivity.pdrtrackflag) {
            for (int i = 0; i < PDRList.size(); i++) {
                preCoord = PDRList.get(i);
                arr = MCoordToDCoord(preCoord[0], preCoord[1]);
                wPDRx = (int) arr[0];
                wPDRy = (int) arr[1];
                canvas.drawBitmap(guijidianBitmap, wPDRx - 8, wPDRy - 8, null);
            }
        }

        if (MainActivity.gpstrackflag) {
            if (MainActivity.gpsCOUNT != 0) {
                preCoord = List.get(MainActivity.gpsCOUNT - 1);// 判断是否和上一个点的坐标一样
                if ((preCoord[0] != MainActivity.X)
                        || (preCoord[1] != MainActivity.Y)) {
                    List.add(MainActivity.gpsCOUNT, new double[]{MainActivity.X,
                            MainActivity.Y});// 将当前位置点添加到List中
                    MainActivity.gpsCOUNT = MainActivity.gpsCOUNT + 1;// 计数器+1
                    for (int i = 0; i < MainActivity.gpsCOUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                } else {
                    for (int i = 0; i < MainActivity.gpsCOUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                }

            } else {
                List.add(MainActivity.gpsCOUNT, new double[]{MainActivity.X,
                        MainActivity.Y});// 将当前位置点添加到List中
                MainActivity.gpsCOUNT = MainActivity.gpsCOUNT + 1;// 计数器+1

                for (int i = 0; i < MainActivity.gpsCOUNT; i++)// 绘制以往位置点到为图上
                {
                    preCoord = List.get(i);
                    arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                    wx = (int) arr[0];
                    wy = (int) arr[1];
                    canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);

                }
            }
            double x = MainActivity.X;
            double y = MainActivity.Y;
            arr = MCoordToWCoord(x, y);
            wx = (int) arr[0];
            wy = (int) arr[1];
            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        } else {
            // 只绘制定位点
            //MainActivity.COUNT = 0;
            List.clear();
            paint.setColor(Color.BLUE);
            paint.setAlpha(200);
            // 让画出的图形是空心的
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint.setStrokeWidth(5);
            // 画圆

            double x = MainActivity.X;
            double y = MainActivity.Y;
            arr = MCoordToWCoord(x, y);

            wx = (int) arr[0];
            wy = (int) arr[1];

            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        }
    }
}

