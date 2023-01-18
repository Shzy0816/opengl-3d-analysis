package com.shenyutao.opengldemo.third;

public class NativeRender {
    public static final int SAMPLE_TYPE = 200;
    public static final int SAMPLE_TYPE_TRIANGLE = SAMPLE_TYPE;
    public static final int SAMPLE_TYPE_3D_MODEL = SAMPLE_TYPE + 1;
    public static final int SAMPLE_TYPE_3D_MODEL_ANIM = SAMPLE_TYPE + 2;
    public static final int SAMPLE_TYPE_TEXT = SAMPLE_TYPE + 3;
    public static final int SAMPLE_TYPE_TEXT_ENGLISH = SAMPLE_TYPE + 4;
    public static final int SAMPLE_TYPE_SET_TOUCH_LOC = SAMPLE_TYPE + 999;
    public static final int SAMPLE_TYPE_SET_GRAVITY_XY = SAMPLE_TYPE + 1000;

    static {
        System.loadLibrary("native-render");
    }

    public native void native_Init(int renderIndex);

    public native void native_UnInit();

    public native void native_SetParams(int paramType, int value0, int value1, String fileName, int renderIndex);

    public native void native_SetParamsFloat(int paramType, float value0, float value1, int renderIndex);

    public native void native_UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY, int renderIndex);

    public native void native_SetImageData(int format, int width, int height, byte[] bytes, int renderIndex);

    public native void native_SetImageDataWithIndex(int index, int format, int width, int height, byte[] bytes, int renderIndex);

    public native void native_SetAudioData(short[] audioData, int renderIndex);

    public native void native_OnSurfaceCreated(int renderIndex);

    public native void native_OnSurfaceChanged(int width, int height, int renderIndex);

    public native void native_OnDrawFrame(int renderIndex);

    public native void native_setViewMatrix(int eyeX, int eyeY, int eyeZ,
                                            int lookAtX, int lookAtY, int lookAtZ,
                                            int upX, int upY, int upZ,
                                            int i);

    public native void native_AdjustPosition(int x, int y, int z, int i);
}
