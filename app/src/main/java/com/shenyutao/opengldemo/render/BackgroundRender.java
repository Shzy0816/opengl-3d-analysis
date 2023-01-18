package com.shenyutao.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BackgroundRender implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 当Surface被创建的时候，
        // GLSurfaceView会调用这个方法，这发生在应用程序创建的第一次，
        // 并且当设备被唤醒或者用户从其他activity切换回去时，也会被调用。

        // GLES20里面的方法全是静态方法

        Log.i("Test", "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 在Surface创建以后，每次Surface尺寸变化后，这个方法都会调用
        GLES20.glViewport(0, 0, width, height);
        Log.i("Test", "onSurfaceChanged");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 当绘制每一帧的时候会被调用。
        GLES20. glClearColor(1f, 0f, 0f, 1f);
        Log.i("Test", "onDrawFrame");
    }
}
