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
    private Bitmap displayBitmap;
    public static ArrayList<double[]> List;// 存放轨迹点坐标的List
    private Canvas canvas;
    private float[] arr;// 用于坐标转换的x、y坐标值
    public double x = 39443571;
    public double y = 4429844;
    public float wx;// 坐标转换后的x
    public float wy;// 坐标转换后的y


    android.graphics.Paint paint = new android.graphics.Paint();

    public AoPDRView(Context context) {
        super(context);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new ArrayList<double[]>();
        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);

    }

    public AoPDRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapFactory bf = new BitmapFactory();
        preCoord = new double[2];
        List = new java.util.ArrayList<double[]>();
        List.add(new double[]{x, y});
        dingweiBitmap = bf.decodeResource(getResources(), R.drawable.dingwei);
        guijidianBitmap = bf.decodeResource(getResources(),
                R.drawable.guijidian);
        displayBitmap = bf.decodeResource(getResources(),
                R.drawable.display);


    }


    public void addPoint(float orient) {
        x += +50 * Math.sin(Math.toRadians(orient));
        y += +50 * Math.cos(Math.toRadians(orient));
        AoPDRView.List.add(new double[]{x, y});
    }

    @Override
    public void onUserBitmapDraw(Bitmap bitmap) {
        super.onUserBitmapDraw(bitmap);
        canvas = new Canvas(bitmap);

        for (int i = 0; i < List.size(); i++) {

            preCoord = List.get(i);
            paint.setColor(Color.BLUE);
            paint.setAlpha(200);
            // 让画出的图形是空心的
            paint.setStyle(android.graphics.Paint.Style.FILL);
            // 设置画出的线的 粗细程度
            paint.setStrokeWidth(5);
            // 画圆


            arr = MCoordToWCoord(preCoord[0], preCoord[1]);

            wx = (float) arr[0];
            wy = (float) arr[1];

            canvas.drawBitmap(guijidianBitmap, wx - 8, wy - 8, null);
        }
    }
}





