package com.shenyutao.opengldemo.render;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.shenyutao.opengldemo.bean.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CubeRender implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private FloatBuffer mCubeVertexBuffer;
    private FloatBuffer mCubeColorBuffer;

    private ShortBuffer mIndexBuffer;
    private int mProgram;

    private MatrixState matrixState;


    private final String vertexShaderCode =
            "attribute vec4 a_Position;     \n"
                    + "uniform mat4 v_Matrix;          \n"
                    + "attribute vec4 a_Color;        \n"
                    + "varying vec4 v_Color;          \n"
                    + "void main() {                  \n"
                    + "   v_Color = a_Color;          \n"
                    + "   gl_Position =  v_Matrix * a_Position;   \n"
                    + "}";

    private final String fragmentShaderCode =
            "precision mediump float; \n" +
                    "varying vec4 v_Color; \n" +
                    "void main() { \n" +
                    "  gl_FragColor = v_Color;\n" +
                    "}";


    //    final float mCubeVertices[] = {
//            -1.0f,1.0f,1.0f,    //正面左上0
//            -1.0f,-1.0f,1.0f,   //正面左下1
//            1.0f,-1.0f,1.0f,    //正面右下2
//            1.0f,1.0f,1.0f,     //正面右上3
//            -1.0f,1.0f,-1.0f,    //反面左上4
//            -1.0f,-1.0f,-1.0f,   //反面左下5
//            1.0f,-1.0f,-1.0f,    //反面右下6
//            1.0f,1.0f,-1.0f,     //反面右上7
//    };
    final short[] mIndex = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };
//
//    float mCubeColors[] = {
//            0f,1f,0f,1f,
//            0f,1f,0f,1f,
//            0f,1f,0f,1f,
//            0f,1f,0f,1f,
//            1f,0f,0f,1f,
//            1f,0f,0f,1f,
//            1f,0f,0f,1f,
//            1f,0f,0f,1f,
//    };

    final float[] mCubeVertices = {
            // Front face
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,

            // Right face
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,

            // Back face
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            // Left face
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,

            // Top face
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,

            // Bottom face
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
    };

    final float[] mCubeColors = {
            // Front face (red)
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            // Right face (green)
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            // Back face (blue)
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            // Left face (yellow)
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,

            // Top face (cyan)
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,

            // Bottom face (magenta)
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f
    };




    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 剔除面，反面的三角形不显示，为了提高性能
        // 从我们(用户)的角度观察，顶点链接顺序为逆时针的三角形为正面，反之为反面
        // 具体参考 https://www.jianshu.com/p/ee04165f2a02
        // GLES20.glEnable(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        // 创建顶点数组缓冲区
        mCubeVertexBuffer = ByteBuffer.allocateDirect(mCubeVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mCubeVertexBuffer.put(mCubeVertices)
                .position(0);


        // 创建颜色缓冲区
        mCubeColorBuffer = ByteBuffer.allocateDirect(mCubeColors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mCubeColorBuffer.put(mCubeColors).position(0);


        // 索引缓冲区
        mIndexBuffer = ByteBuffer.allocateDirect(mIndex.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        mIndexBuffer.put(mIndex).position(0);

        // 创建顶点器和着色器 并获取它们的句柄
        // 根据句柄为顶点器和着色器设置代码
        // 编译顶点器和着色器
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        // 创建程序，加入两个Shader
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);


        // 链接程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int vMatrix = GLES20.glGetUniformLocation(mProgram, "v_Matrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrixState.getMVPMatrix(), 0);
        int vPosition = GLES20.glGetAttribLocation(mProgram, "a_Position");
        GLES20.glEnableVertexAttribArray(vPosition);
        // stride指定从一个属性到下一个属性的字节跨度
        GLES20.glVertexAttribPointer(vPosition, 3,
                GLES20.GL_FLOAT, false, 12, mCubeVertexBuffer);
        int vColor = GLES20.glGetAttribLocation(mProgram, "a_Color");
        GLES20.glEnableVertexAttribArray(vColor);
        GLES20.glVertexAttribPointer(vColor, 4, GLES20.GL_FLOAT, false, 16, mCubeColorBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }


    private float xAngle = 0f;
    private float yAngle = 0f;

    public void setAngle(float xAngle,float yAngle){
        this.xAngle += xAngle;
        this.yAngle += yAngle;
        this.xAngle %= 360;
        this.yAngle %= 360;
    }


    public void setMatrixState(MatrixState matrixState) {
        this.matrixState = matrixState;
    }

    public MatrixState getMatrixState() {
        return matrixState;
    }
}
