package com.example.wanghanqing.godbless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.AoGIS.render.AoView;

import java.util.ArrayList;

/**
 * Created by wanghanqing on 2017/7/18.
 */

public class AoGPSView extends AoView {


    private double[] preCoord;
    private Bitmap dingweiBitmap;
    private Bitmap guijidianBitmap;
    private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    private Canvas canvas;
    private float[] arr;// 用于坐标转换的x、y坐标值
    public double x;
    public double y;
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
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoGPSView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoGPSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    android.graphics.Paint paint = new android.graphics.Paint();


    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);


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


        arr = MCoordToWCoord(x, y);

        wx = (float) arr[0];
        wy = (float) arr[1];

        canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
    }
}



