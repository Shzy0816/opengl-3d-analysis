package com.shenyutao.opengldemo.tool;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.WindowManager;

public class ScreenTools {

    public static Point getWindow(Context context) {
        Point screenPoint = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //获取屏幕的真实尺寸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Rect bounds = windowManager.getCurrentWindowMetrics().getBounds();
            screenPoint.x = bounds.right;
            screenPoint.y = bounds.bottom;
        } else {
            windowManager.getDefaultDisplay().getSize(screenPoint);
        }
        return screenPoint;
    }
}
