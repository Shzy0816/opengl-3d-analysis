package com.shenyutao.opengldemo.third;

import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_SET_GRAVITY_XY;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_SET_TOUCH_LOC;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.shenyutao.opengldemo.callback.AnimationCallback;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AnimationRender implements GLSurfaceView.Renderer {
    private static final String TAG = "AnimationRender";
    public static final int TYPE_ANIMATION = 0;
    public static final int TYPE_BACKGROUND = 1;
    private final int renderType;

    private final NativeRender mNativeRender;
    private int mSampleType;
    private int frameCount = 0;
    private long firstCheckTime = 0;
    private AnimationCallback animationCallback;

    AnimationRender(int renderType) {
        this.renderType = renderType;
        mNativeRender = new NativeRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mNativeRender.native_OnSurfaceCreated(renderType);
        Log.e(TAG, "onSurfaceCreated() called with: GL_VERSION = [" + gl.glGetString(GL10.GL_VERSION) + "]");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mNativeRender.native_OnSurfaceChanged(width, height, renderType);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mNativeRender.native_OnDrawFrame(renderType);
        checkFPS();
        if (animationCallback != null) {
            animationCallback.onAnimationStart();
        }
    }


    private void checkFPS() {
        if (frameCount == 0) {
            firstCheckTime = System.currentTimeMillis();
        } else if (frameCount == 60) {
            long currentTimeMillis = System.currentTimeMillis();
            long timeGap = currentTimeMillis - firstCheckTime;
            int fps = (int) (frameCount * 1000.0f / timeGap);
            frameCount = 0;
            firstCheckTime = currentTimeMillis;

            if (mFPSListener != null) {
                mFPSListener.onFpsUpdate(fps);
            }
        }
        frameCount++;
    }

    private FPSListener mFPSListener;

    public void setOnFPSListener(FPSListener fpsListener) {
        mFPSListener = fpsListener;
    }

    public interface FPSListener {
        void onFpsUpdate(int fps);
    }

    public void init() {
        mNativeRender.native_Init(renderType);
    }

    public void unInit() {
        mNativeRender.native_UnInit();
    }

    public void setParams(int paramType, int value0, int value1, String fileName) {
        if (paramType == SAMPLE_TYPE) {
            mSampleType = value0;
        }
        mNativeRender.native_SetParams(paramType, value0, value1, fileName, renderType);
    }

    public void setTouchLoc(float x, float y) {
        mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_TOUCH_LOC, x, y, renderType);

    }

    public void setGravityXY(float x, float y) {
        mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_GRAVITY_XY, x, y, renderType);

    }

    public void setImageData(int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageData(format, width, height, bytes, renderType);

    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageDataWithIndex(index, format, width, height, bytes, renderType);

    }

    public void setAudioData(short[] audioData) {
        mNativeRender.native_SetAudioData(audioData, renderType);

    }

    public int getSampleType() {
        return mSampleType;
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
        mNativeRender.native_UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY, renderType);
    }

    public void setAnimationCallback(AnimationCallback animationCallback) {
        this.animationCallback = animationCallback;
    }

    public void setViewMatrix(int eyeX, int eyeY, int eyeZ,
                              int lookAtX, int lookAtY, int lookAtZ,
                              int upX, int upY, int upZ) {
        mNativeRender.native_setViewMatrix(eyeX, eyeY, eyeZ,
                lookAtX, lookAtY, lookAtZ,
                upX, upY, upZ,
                renderType);
    }

    public void adjustPosition(int x, int y, int z) {
        mNativeRender.native_AdjustPosition(x, y, z, renderType);
    }
}
