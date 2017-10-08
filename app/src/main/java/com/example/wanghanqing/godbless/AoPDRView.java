package com.example.wanghanqing.godbless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.AoGIS.render.AoView;

import java.util.ArrayList;

/**
 * Created by wanghanqing on 2017/7/18.
 */

public class AoPDRView extends AoView {


    private double[] preCoord;
    private Bitmap dingweiBitmap;
    private Bitmap guijidianBitmap;
    //private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    private Canvas canvas;
    private float[] arr;// 用于坐标转换的x、y坐标值
    //    public double x = 39443571;
//    public double y = 4429844;
    public float wx;// 坐标转换后的x
    public float wy;// 坐标转换后的y


    android.graphics.Paint paint = new android.graphics.Paint();

    public AoPDRView(Context context) {
        super(context);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
//        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);


    }


    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);
        // 进行轨迹点绘制
        if (DeadReackoningActivity.flag) {
            if (DeadReackoningActivity.count != 0) {
                preCoord = List.get(DeadReackoningActivity.count - 1);// 判断是否和上一个点的坐标一样
                if ((preCoord[0] != DeadReackoningActivity.X)
                        || (preCoord[1] != DeadReackoningActivity.Y)) {
                    List.add(DeadReackoningActivity.count, new double[]{DeadReackoningActivity.X,
                            DeadReackoningActivity.Y});// 将当前位置点添加到List中
                    DeadReackoningActivity.count = DeadReackoningActivity.count + 1;// 计数器+1
                    for (int i = 0; i < DeadReackoningActivity.count; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                } else {
                    for (int i = 0; i < DeadReackoningActivity.count; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                }

            } else {
                List.add(DeadReackoningActivity.count, new double[]{DeadReackoningActivity.X,
                        DeadReackoningActivity.Y});// 将当前位置点添加到List中
                DeadReackoningActivity.count = DeadReackoningActivity.count + 1;// 计数器+1

                for (int i = 0; i < GPSTrackActivity.COUNT; i++)// 绘制以往位置点到为图上
                {
                    preCoord = List.get(0);
                    arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                    wx = (int) arr[0];
                    wy = (int) arr[1];
                    canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);

                }
            }
            double x = DeadReackoningActivity.X;
            double y = DeadReackoningActivity.Y;
            arr = MCoordToWCoord(x, y);
            wx = (int) arr[0];
            wy = (int) arr[1];
            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        } else {
            // 只绘制定位点
            DeadReackoningActivity.count = 0;
            List.clear();
            paint.setColor(Color.BLUE);
            paint.setAlpha(200);
            // 让画出的图形是空心的
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint.setStrokeWidth(5);
            // 画圆

            double x = DeadReackoningActivity.X;
            double y = DeadReackoningActivity.Y;
            arr = MCoordToWCoord(x, y);

            wx = (int) arr[0];
            wy = (int) arr[1];

            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        }
    }
}





