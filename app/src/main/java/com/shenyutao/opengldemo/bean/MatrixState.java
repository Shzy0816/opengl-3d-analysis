package com.shenyutao.opengldemo.bean;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MatrixState {
    private static final int BIT_PER_FLOAT = 4;
    private boolean recountMVPMatrix = false;
    /**
     * 总矩阵 mViewMatrix和mModelMatrix以及mProjectionMatrix的乘积
     */
    private final float[] mMVPMatrix = new float[16];
    /**
     * 控制相机朝向，位置的矩阵
     */
    private final float[] mViewMatrix = new float[16];
    /**
     * 控制平移，旋转，缩放的矩阵
     */
    private final float[] mModelMatrix = new float[16];
    /**
     * 控制透视投影的矩阵(透视投影或者正交投影)
     */
    private final float[] mProjectionMatrix = new float[16];
    /**
     * 控制相机位置的矩阵
     */
    private float[] cameraLocation = new float[3];
    /**
     * 控制光源位置的举证
     */
    private float[] lightLocation = new float[]{-100f, 0f, -100f};
    /**
     * 相机位置举证对应的Buffer
     */
    private FloatBuffer cameraLocationBuffer;
    /**
     * 光源位置矩阵对应的Buffer
     */
    private FloatBuffer lightLocationBuffer;

    /**
     * 构造方法私有化，防止直接创建该类，容易出现空值异常
     */
    private MatrixState() {
    }

    /**
     * 相机视距范围
     */
    private float near;
    private float far;


    /**
     * 用于设置默认的ViewMatrix
     */
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = -50f;
    private float lookAtX = 0.0f;
    private float lookAtY = 0.0f;
    private float lookAtZ = 0.0f;
    private float upX = 0.0f;
    private float upY = 1.0f;
    private float upZ = 0.0f;

    /**
     * 获取一个默认参数的MatrixState
     *
     * @return MatrixState
     */
    public static MatrixState getDefaultMatrixState(int scanWidth, int scanHeight) {
        MatrixState matrixState = new MatrixState();
        // 设置默认的光源位置
        matrixState.setLightLocation(-10000f, 10000f, -10000f);
        float cameraX = 0.0f;
        float cameraY = 0.0f;
        float cameraZ = -50f;
        float lookAtX = 0.0f;
        float lookAtY = 0.0f;
        float lookAtZ = 0.0f;
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        matrixState.setViewMatrix(cameraX, cameraY, cameraZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
        matrixState.setProjectionMatrix(scanWidth, scanHeight, 1f, 2000f);
        matrixState.setModelMatrix(0f, 0f, 0f, 0f, 0f);
        return matrixState;
    }


    public void setCameraPositionZ(float cameraZ) {
        this.cameraZ = cameraZ;
        setViewMatrix(cameraX, cameraY, this.cameraZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
    }

    /**
     * 设置ViewMatrix
     *
     * @param cameraX 相机位置X坐标
     * @param cameraY 相机位置Y坐标
     * @param cameraZ 相机位置Z坐标
     * @param lookAtX 目标位置X坐标
     * @param lookAtY 目标位置Y坐标
     * @param lookAtZ 目标位置Z坐标
     * @param upX     相机朝向向量的X分量
     * @param upY     相机朝向向量的Y分量
     * @param upZ     相机朝向向量的Z分量
     */
    public void setViewMatrix(float cameraX, float cameraY, float cameraZ,
                              float lookAtX, float lookAtY, float lookAtZ,
                              float upX, float upY, float upZ) {
        // 设置camera位置
        setCameraLocation(cameraX, cameraY, cameraZ);
        // 设置mViewMatrix
        Matrix.setLookAtM(mViewMatrix, 0, cameraX, cameraY, cameraZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
        recountMVPMatrix = true;
    }

    public void setViewMatrix(float cameraX, float cameraY, float cameraZ) {
        // 设置camera位置
        setCameraLocation(cameraX, cameraY, cameraZ);
        // 设置mViewMatrix
        Matrix.setLookAtM(mViewMatrix, 0, cameraX, cameraY, cameraZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
        recountMVPMatrix = true;
    }

    /**
     * 设置ProjectionMatrix
     *
     * @param scanWidth  屏幕宽度
     * @param scanHeight 屏幕高度
     */
    public void setProjectionMatrix(int scanWidth, int scanHeight, float near, float far) {
        float ratio = (float) scanWidth / scanHeight;
        float left = -ratio;
        float right = ratio;
        float bottom = -1.0f;
        float top = 1.0f;
        this.near = near;
        this.far = far;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        recountMVPMatrix = true;
    }


    private float translateX;
    private float translateY;
    private float translateZ;

    /**
     * 待修改
     *
     * @param translateX
     * @param translateY
     * @param translateZ
     * @param rotateAngleX
     * @param rotateAngleY
     */
    public void setModelMatrix(float translateX, float translateY, float translateZ,
                               float rotateAngleX, float rotateAngleY) {
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, rotateAngleX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateAngleY, 1.0f, 0.0f, 0.0f);
        Matrix.translateM(mModelMatrix, 0, translateX, translateY, translateZ);
        recountMVPMatrix = true;
    }


    /**
     * 圆心运动
     *
     * @param translateX
     * @param translateY
     * @param translateZ
     * @param rotateAngleX
     * @param rotateAngleY
     */
    public void setCenterMotionModelMatrix(float translateX, float translateY, float translateZ,
                                           float rotateAngleX, float rotateAngleY, float rotateAngleZ,
                                           float rx, float ry, float rz) {
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        Matrix.setIdentityM(mModelMatrix, 0);
        // 改变圆心
        Matrix.translateM(mModelMatrix, 0, translateX, translateY, translateZ);
        Matrix.rotateM(mModelMatrix, 0, rotateAngleX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateAngleY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateAngleZ, 0.0f, 1.0f, 0.0f);
        // 改变半径
        Matrix.translateM(mModelMatrix, 0, rx, ry, rz);
        recountMVPMatrix = true;
    }


    /**
     * @param rotateAngle
     * @param x
     * @param y
     * @param z
     */
    public void rotateByModelMatrix(float rotateAngle, float x, float y, float z) {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, translateX, translateY, translateZ);
        Matrix.rotateM(mModelMatrix, 0, rotateAngle, x, y, z);
        recountMVPMatrix = true;
    }

    /**
     * 计算总矩阵
     */
    private void countMVPMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
    }


    private void initCameraLocationBuffer() {
        cameraLocationBuffer = ByteBuffer.allocateDirect(cameraLocation.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        cameraLocationBuffer.put(cameraLocation).position(0);
    }

    private void initLightLocationBuffer() {
        lightLocationBuffer = ByteBuffer.allocateDirect(lightLocation.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        lightLocationBuffer.put(lightLocation).position(0);
    }


    public float[] getMVPMatrix() {
        if (recountMVPMatrix) {
            countMVPMatrix();
        }
        return mMVPMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    private void setCameraLocation(float cameraLocationX, float cameraLocationY, float cameraLocationZ) {
        this.cameraLocation[0] = cameraLocationX;
        this.cameraLocation[1] = cameraLocationY;
        this.cameraLocation[2] = cameraLocationZ;
        cameraX = cameraLocationX;
        cameraY = cameraLocationY;
        cameraZ = cameraLocationZ;
        initCameraLocationBuffer();
    }

    public void setLightLocation(float lightLocationX, float lightLocationY, float lightLocationZ) {
        this.lightLocation[0] = lightLocationX;
        this.lightLocation[1] = lightLocationY;
        this.lightLocation[2] = lightLocationZ;
        initLightLocationBuffer();
    }

    public FloatBuffer getCameraLocationBuffer() {
        return cameraLocationBuffer;
    }

    public FloatBuffer getLightLocationBuffer() {
        return lightLocationBuffer;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public float getCameraZ() {
        return cameraZ;
    }

    public float[] getCameraLocation() {
        return cameraLocation;
    }
}
