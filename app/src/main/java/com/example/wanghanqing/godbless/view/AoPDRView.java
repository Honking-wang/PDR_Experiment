package com.example.wanghanqing.godbless.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.AoGIS.render.AoView;
import com.example.wanghanqing.godbless.R;
import com.example.wanghanqing.godbless.activity.ExpPDRActivity;

import java.util.ArrayList;

/**
 * Created by wanghanqing on 2017/7/18.
 */

public class AoPDRView extends AoView {


    private double[] preCoord;
    private double[] preCoord2;
    private Bitmap dingweiBitmap;
    private Bitmap dingweiBitmap2;
    private Bitmap guijidianBitmap;
    private Bitmap guijidianBitmap2;
    //private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    public static ArrayList<double[]> List2;
    private Canvas canvas;
    private Canvas canvas2;
    private float[] arr;// 用于坐标转换的x、y坐标值
    private float[] arr2;
    public float wx;// 坐标转换后的x
    public float wy;// 坐标转换后的y
    public float wgx;// 坐标转换后的x
    public float wgy;// 坐标转换后的y


    android.graphics.Paint paint = new android.graphics.Paint();
    android.graphics.Paint paint2 = new android.graphics.Paint();

    public AoPDRView(Context context) {
        super(context);
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory bf2 = new BitmapFactory();
        preCoord = new double[2];
        preCoord2 = new double[2];
        List = new ArrayList<double[]>();
        List2 = new ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        dingweiBitmap2 = bf2.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(), R.drawable.pdrguijidian);
        guijidianBitmap2 = bf2.decodeResource(getResources(), R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory bf2 = new BitmapFactory();
        preCoord = new double[2];
        preCoord2 = new double[2];
        List = new ArrayList<double[]>();
        List2 = new ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        dingweiBitmap2 = bf2.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(), R.drawable.pdrguijidian);
        guijidianBitmap2 = bf2.decodeResource(getResources(), R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory bf2 = new BitmapFactory();
        preCoord = new double[2];
        preCoord2 = new double[2];
        List = new ArrayList<double[]>();
        List2 = new ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        dingweiBitmap2 = bf2.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(), R.drawable.pdrguijidian);
        guijidianBitmap2 = bf2.decodeResource(getResources(), R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);


    }


    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);
        canvas2 = new Canvas(bitmap);
        // 进行轨迹点绘制
        if (ExpPDRActivity.flag) {
            if (ExpPDRActivity.count != 0) {
                preCoord = List.get(ExpPDRActivity.count - 1);// 判断是否和上一个点的坐标一样
                if ((preCoord[0] != ExpPDRActivity.PX)
                        || (preCoord[1] != ExpPDRActivity.PY)) {
                    List.add(ExpPDRActivity.count, new double[]{ExpPDRActivity.PX,
                            ExpPDRActivity.PY});// 将当前位置点添加到List中
                    ExpPDRActivity.count = ExpPDRActivity.count + 1;// 计数器+1
                    for (int i = 0; i < ExpPDRActivity.count; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                } else {
                    for (int i = 0; i < ExpPDRActivity.count; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                }

            } else {
                List.add(ExpPDRActivity.count, new double[]{ExpPDRActivity.PX,
                        ExpPDRActivity.PY});// 将当前位置点添加到List中
                ExpPDRActivity.count = ExpPDRActivity.count + 1;// 计数器+1

                for (int i = 0; i < ExpPDRActivity.count; i++)// 绘制以往位置点到为图上
                {
                    preCoord = List.get(0);
                    arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                    wx = (int) arr[0];
                    wy = (int) arr[1];
                    canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);

                }
            }
            double x = ExpPDRActivity.PX;
            double y = ExpPDRActivity.PY;
            arr = MCoordToWCoord(x, y);
            wx = (int) arr[0];
            wy = (int) arr[1];
            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        } else {
            // 只绘制定位点
            ExpPDRActivity.count = 0;
            List.clear();
            paint.setColor(Color.BLUE);
            paint.setAlpha(200);
            // 让画出的图形是空心的
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint.setStrokeWidth(5);
            // 画圆

            double x = ExpPDRActivity.PX;
            double y = ExpPDRActivity.PY;
            arr = MCoordToWCoord(x, y);

            wx = (int) arr[0];
            wy = (int) arr[1];

            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        }


        if (ExpPDRActivity.FLAG) {
            if (ExpPDRActivity.COUNT != 0) {
                //获取到list里最新的点
                preCoord2 = List2.get(ExpPDRActivity.COUNT - 1);
                // 判断是否和上一个点的坐标一样
                if ((preCoord2[0] != ExpPDRActivity.X)
                        || (preCoord2[1] != ExpPDRActivity.Y)) {
                    List2.add(ExpPDRActivity.COUNT, new double[]{ExpPDRActivity.X,
                            ExpPDRActivity.Y});// 将当前位置点添加到List中，index为count的位置
                    ExpPDRActivity.COUNT = ExpPDRActivity.COUNT + 1;// 计数器+1
                    for (int i = 0; i < ExpPDRActivity.COUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord2 = List2.get(i);
                        arr2 = MCoordToWCoord(preCoord2[0], preCoord2[1]);
                        wgx = (int) arr2[0];
                        wgy = (int) arr2[1];
                        canvas2.drawBitmap(guijidianBitmap2, wgx - 8, wgy - 8, null);
                    }
                } else {
                    for (int i = 0; i < ExpPDRActivity.COUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord2 = List2.get(i);
                        arr2 = MCoordToWCoord(preCoord2[0], preCoord2[1]);
                        wgx = (int) arr2[0];
                        wgy = (int) arr2[1];
                        canvas2.drawBitmap(guijidianBitmap2, wgx - 8, wgy - 8, null);
                    }
                }

            } else {
                List2.add(ExpPDRActivity.COUNT, new double[]{ExpPDRActivity.X,
                        ExpPDRActivity.Y});// 将当前位置点添加到List中
                ExpPDRActivity.COUNT = ExpPDRActivity.COUNT + 1;// 计数器+1

                for (int i = 0; i < ExpPDRActivity.COUNT; i++)// 绘制以往位置点到为图上
                {
                    preCoord2 = List2.get(i);
                    arr2 = MCoordToWCoord(preCoord2[0], preCoord2[1]);
                    wgx = (int) arr2[0];
                    wgy = (int) arr2[1];
                    canvas2.drawBitmap(guijidianBitmap2, wgx - 8, wgy - 8, null);

                }
            }
            double x = ExpPDRActivity.X;
            double y = ExpPDRActivity.Y;
            arr2 = MCoordToWCoord(x, y);
            wgx = (int) arr2[0];
            wgy = (int) arr2[1];
            canvas2.drawBitmap(dingweiBitmap2, wgx - 8, wgy - 8, null);
        } else {
            // 只绘制定位点
            ExpPDRActivity.COUNT = 0;
            List2.clear();
            paint2.setColor(Color.RED);
            paint2.setAlpha(200);
            // 让画出的图形是空心的
            paint2.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint2.setStrokeWidth(5);
            // 画圆

            double gx = ExpPDRActivity.X;
            double gy = ExpPDRActivity.Y;
            arr2 = MCoordToWCoord(gx, gy);

            wgx = (int) arr2[0];
            wgy = (int) arr2[1];

            canvas2.drawBitmap(dingweiBitmap2, wgx - 8, wgy - 8, null);
        }


        paint2.setColor(Color.RED);
        paint2.setAlpha(200);
        // 让画出的图形是空心的
        paint2.setStyle(android.graphics.Paint.Style.FILL);
        // 设置画出的线的 粗细程度
        paint2.setStrokeWidth(5);
        // 画圆

        double gx = ExpPDRActivity.X;
        double gy = ExpPDRActivity.Y;
        arr2 = MCoordToWCoord(gx, gy);

        wgx = (int) arr2[0];
        wgy = (int) arr2[1];

        canvas.drawBitmap(dingweiBitmap2, wgx - 8, wgy - 8, null);
    }
}





