package com.shenyutao.opengldemo.third;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;

import com.shenyutao.opengldemo.render.ObjFileRender;

public class BackGroundSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {

    private ObjFileRender objFileRender;
    private ScaleGestureDetector mScaleGestureDetector;

    public BackGroundSurfaceView(Context context, ObjFileRender objFileRender, boolean isTransparent) {
        this(context, objFileRender, null, isTransparent);
    }

    public BackGroundSurfaceView(Context context, ObjFileRender objFileRender, AttributeSet attrs, boolean isTransparent) {
        super(context, attrs);
        this.setEGLContextClientVersion(2);
        this.objFileRender = objFileRender;

        if (isTransparent) {
            setTranslucent();
        }
        setRenderer(objFileRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    public void setTranslucent() {
        // 设置背景透明，否则一般加载时间长的话会先黑一下，但是也有问题，就是在它之上无法再有View了，因为它是top的，用的时候需要注意，必要的时候将其设置为false
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }
}
