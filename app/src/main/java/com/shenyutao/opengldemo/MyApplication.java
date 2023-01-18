package com.shenyutao.opengldemo;

import android.app.Application;


import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author ShangMeichun
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Window window;

    public static Window getWindow() {
        return window;
    }

    public static void setWindow(Window window) {
        MyApplication.window = window;
    }

    /**
     * 存储屏幕的真实尺寸
     */
    private static final Point screenPoint = new Point();


    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //获取屏幕的真实尺寸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Rect bounds = windowManager.getCurrentWindowMetrics().getBounds();
            screenPoint.x = bounds.right;
            screenPoint.y = bounds.bottom;
        } else {
            windowManager.getDefaultDisplay().getSize(screenPoint);
        }
    }


    public static Point getScreenPoint() {
        return screenPoint;
    }
}
