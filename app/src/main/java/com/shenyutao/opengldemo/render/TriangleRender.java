package com.shenyutao.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Shzy
 */
public class TriangleRender implements GLSurfaceView.Renderer {
    private int program;
    private FloatBuffer vertexBuffer;

    private final String vertexShaderCode =
            "attribute vec4 vPosition; " +
                    "void main() {" +
                    "gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    static float[] triangleCoords = {
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    //设置颜色，依次为红绿蓝和透明通道
    float[] color = {1.0f, 0f, 0f, 1.0f};

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // 创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 重新整理一下内存
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 将数组放入缓冲区
        vertexBuffer.put(triangleCoords);
        // 设置偏移量为0
        vertexBuffer.position(0);

        // 创建一个顶点器，并返回该程序的句柄
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // 根据句柄为该程序添加代码
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        // 创建一个着色器 ， 同顶点程序
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        // 创建程序，链接顶点器和着色器
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(program);


        // 获取vPosition变量句柄
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        // 获取vColor变量句柄
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        // 允许CPU向GPU发送缓冲区对象
        GLES20.glEnableVertexAttribArray(positionHandle);
        // 将triangleCoords装填到vPosition
        GLES20.glVertexAttribPointer(positionHandle, 3,
                GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        // 将color数组装填到vColor
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
