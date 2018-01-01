package com.example.wanghanqing.godbless.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.AoGIS.render.AoView;
import com.example.wanghanqing.godbless.activity.ExpGPSActivity;
import com.example.wanghanqing.godbless.R;

import java.util.ArrayList;

/**
 * Created by wanghanqing on 2017/7/18.
 */

public class AoGPSView extends AoView {


    private double[] preCoord;
    private Bitmap dingweiBitmap;
    private Bitmap guijidianBitmap;
//  private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    private Canvas canvas;
    private float[] arr;// 用于坐标转换的x、y坐标值
//    public double x;
//    public double y;
    public float wx;// 坐标转换后的x
    public float wy;// 坐标转换后的y


    public AoGPSView(Context context) {
        super(context);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }


    public AoGPSView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }


    public AoGPSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
//        displayBitmap = bf.decodeResource(getResources(),
//                R.drawable.display);

    }


    android.graphics.Paint paint = new android.graphics.Paint();
//    android.graphics.Paint paint1 = new android.graphics.Paint();



    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);
        // 进行轨迹点绘制
        if (ExpGPSActivity.FLAG) {
            if (ExpGPSActivity.COUNT != 0) {//获取到list里最新的点
                preCoord = List.get(ExpGPSActivity.COUNT - 1);// 判断是否和上一个点的坐标一样
                if ((preCoord[0] != ExpGPSActivity.GX)
                        || (preCoord[1] != ExpGPSActivity.GY)) {
                    List.add(ExpGPSActivity.COUNT, new double[]{ExpGPSActivity.GX,
                            ExpGPSActivity.GY});// 将当前位置点添加到List中，index为count的位置
                    ExpGPSActivity.COUNT = ExpGPSActivity.COUNT + 1;// 计数器+1
                    for (int i = 0; i < ExpGPSActivity.COUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                } else {
                    for (int i = 0; i < ExpGPSActivity.COUNT; i++)// 绘制以往位置点到为图上
                    {
                        preCoord = List.get(i);
                        arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                        wx = (int) arr[0];
                        wy = (int) arr[1];
                        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
                    }
                }

            } else {
                List.add(ExpGPSActivity.COUNT, new double[]{ExpGPSActivity.GX,
                        ExpGPSActivity.GY});// 将当前位置点添加到List中
                ExpGPSActivity.COUNT = ExpGPSActivity.COUNT + 1;// 计数器+1

                for (int i = 0; i < ExpGPSActivity.COUNT; i++)// 绘制以往位置点到为图上
                {
                    preCoord = List.get(i);
                    arr = MCoordToWCoord(preCoord[0], preCoord[1]);
                    wx = (int) arr[0];
                    wy = (int) arr[1];
                    canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);

                }
            }
            double x = ExpGPSActivity.GX;
            double y = ExpGPSActivity.GY;
            arr = MCoordToWCoord(x, y);
            wx = (int) arr[0];
            wy = (int) arr[1];
            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        } else {
            // 只绘制定位点
            ExpGPSActivity.COUNT = 0;
            List.clear();
            paint.setColor(Color.BLUE);
            paint.setAlpha(200);
            // 让画出的图形是空心的
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint.setStrokeWidth(5);
            // 画圆

            double x = ExpGPSActivity.GX;
            double y = ExpGPSActivity.GY;
            arr = MCoordToWCoord(x, y);

            wx = (int) arr[0];
            wy = (int) arr[1];

            canvas.drawBitmap(dingweiBitmap, wx - 8, wy - 8, null);
        }
//        if (ExpGPSActivity.DISPLAY)//如果开启了轨迹显示
//        {
//            for (int i = 0; i < ExpGPSActivity.displayList.size(); i++)// 绘制以往位置点到为图上
//            {
//                preCoord = ExpGPSActivity.displayList.get(i);
//                arr = MCoordToWCoord(preCoord[0], preCoord[1]);
//                wx = (int) arr[0];
//                wy = (int) arr[1];
//                canvas.drawBitmap(displayBitmap, wx - 8, wy - 8, null);
//            }
//        }
    }
}










