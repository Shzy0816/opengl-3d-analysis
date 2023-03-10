package com.shenyutao.opengldemo.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.ImageView;

import com.shenyutao.opengldemo.bean.MatrixState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class ImageRender implements GLSurfaceView.Renderer {

    public ImageRender(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    private Bitmap mBitmap;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int vertexShader;
    private int fragmentShader;


    MatrixState matrixState;

    private int program;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;     \n"
                    + "uniform mat4 vMatrix; \n"
                    + "attribute vec2 vCoordinate;          \n"
                    + "varying vec2 aCoordinate;          \n"
                    + "void main() {                  \n"
                    + "   gl_Position=vMatrix * vPosition;          \n"
                    + "   aCoordinate=vCoordinate;   \n"
                    + "}";

    private final String fragmentShaderCode =
            "precision mediump float; \n" +
                    "uniform sampler2D vTexture; \n" +
                    "varying vec2 aCoordinate; \n" +
                    "void main() { \n" +
                    "  gl_FragColor=texture2D(vTexture,aCoordinate); \n" +
                    "}";

    final float[] mVertices = {
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

    private ShortBuffer shortBuffer;
    final short[] mIndex = {
            6, 7, 4, 6, 4, 5,    //??????
            6, 3, 7, 6, 2, 3,    //??????
            6, 5, 1, 6, 1, 2,    //??????
            0, 3, 2, 0, 2, 1,    //??????
            0, 1, 5, 0, 5, 4,    //??????
            0, 7, 3, 0, 4, 7,    //??????
    };


    final float[] mTextureVertices = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(mVertices);
        vertexBuffer.position(0);

        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(mTextureVertices.length * 4);
        byteBuffer1.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer1.asFloatBuffer();
        textureBuffer.put(mTextureVertices);
        textureBuffer.position(0);

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);


        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);


        createTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);
        int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        int vCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        int vTexture = GLES20.glGetUniformLocation(program, "vTexture");
        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(vCoordinate);

        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrixState.getMVPMatrix(), 0);

        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
        GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

        GLES20.glUniform1i(vTexture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(vCoordinate);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //?????????????????????????????????bitmap
            GLES20.glGenTextures(1, texture, 0);
            //????????????
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //??????????????????S????????????????????????[1/2n,1-1/2n]???????????????????????????border??????
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //??????????????????T????????????????????????[1/2n,1-1/2n]???????????????????????????border??????
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //???bitmap?????????????????????
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    public void setBitmap(Bitmap bitmap) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = bitmap;
        createTexture();
    }


    public void setMatrixState(MatrixState matrixState) {
        this.matrixState = matrixState;
    }

    public MatrixState getMatrixState() {
        return matrixState;
    }
}
