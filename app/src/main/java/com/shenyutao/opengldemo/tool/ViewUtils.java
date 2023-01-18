package com.shenyutao.opengldemo.tool;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.shenyutao.opengldemo.MyApplication;


public class ViewUtils {

    /**
     * 最小滑动距离
     */
    public static final int MIN_FLING_DISTANCE = 50;
    /**
     * 最小滑动速度
     */
    public static final int MIN_FLING_VELOCITY = 0;
    private static final String TAG = "ViewUtils";


    public static float px2dp(float px, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    /**
     * 修改背景的透明度
     *
     * @param alpha    要修改的alpha值，范围为0~1，0代表完全透明
     * @param activity 传入要修改背景alpha值的activity
     */
    public static void setBackgroundAlpha(float alpha, Activity activity) {
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.alpha = alpha;
        activity.getWindow().setAttributes(layoutParams);
    }

    /**
     * 修改背景的透明度
     *
     * @param alpha 要修改的alpha值，范围为0~1，0代表完全透明
     */
    public static void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams layoutParams = MyApplication.getWindow().getAttributes();
        layoutParams.alpha = alpha;
        MyApplication.getWindow().setAttributes(layoutParams);
    }


    /**
     * 传入context对象以及要生成的xml文件，生成一个popupWindow
     *
     * @param context          传入要使用该popupWindow的context对象
     * @param layoutResourceId popupWindow的xml文件
     * @return 生成好的popupWindow，默认高度自适应，宽度match_parent
     */
    public static PopupWindow createPopupWindow(Context context, int layoutResourceId , int animationStyle) {
        View view = LayoutInflater.from(context).inflate(layoutResourceId, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(animationStyle);
        return popupWindow;
    }

}
