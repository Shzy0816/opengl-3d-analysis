package com.shenyutao.opengldemo.bean;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

import com.shenyutao.opengldemo.tool.OpenGLTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProgramInfo {
    private int programHandle;
    private int vertexShader;
    private int fragmentShader;
    private final String vertexShaderCode;
    private final String fragmentShaderCode;

    // 顶点器变量句柄
    private int positionHandle;
    private int normalHandle;
    private int MVPMatrixHandle;
    private int ModelMatrixHandle;
    private int lightLocationHandle;
    private int textureHandle;
    private int cameraLocationHandle;
    private int kdColorHandle;
    private int kaHandle;
    private int kdHandle;
    private int ksHandle;


    public ProgramInfo(String vertexShaderCode, String fragmentShaderCode) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
    }

    public static ProgramInfo loadProgramByFile(File vertexFile, File fragmentFile) {
        try (BufferedReader vertexBuffered = new BufferedReader(new FileReader(vertexFile));
             BufferedReader fragmentBuffered = new BufferedReader(new FileReader(fragmentFile));
        ) {
            return loadProgramByBuffered(vertexBuffered, fragmentBuffered);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ProgramInfo loadProgramFromAssets(String vertexFileName, String fragmentFileName, Context context) throws Error {
        Log.i("Test", "loadProgramFromAssets: " + "loadProgramFromAssets");
        try {
            AssetManager assetManager = context.getAssets();
            InputStream vertexInputStream = assetManager.open(vertexFileName);
            InputStream fragmentInputStream = assetManager.open(fragmentFileName);
            return loadProgramByInputStream(vertexInputStream, fragmentInputStream);
        } catch (IOException e) {
            throw new Error(e.toString());
        }
    }


    private static ProgramInfo loadProgramByInputStream(InputStream vertexInputStream, InputStream fragmentInputStream) {
        try (BufferedReader vertexBuffered = new BufferedReader(new InputStreamReader(vertexInputStream));
             BufferedReader fragmentBuffered = new BufferedReader(new InputStreamReader(fragmentInputStream))) {
            return loadProgramByBuffered(vertexBuffered, fragmentBuffered);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ProgramInfo loadProgramByBuffered(BufferedReader vertexBufferedReader, BufferedReader fragmentBufferedReader) throws IOException {
        String line;
        StringBuilder vertexShaderCode = new StringBuilder();
        StringBuilder fragmentShaderCode = new StringBuilder();
        // 加载顶点器文件代码
        while ((line = vertexBufferedReader.readLine()) != null) {
            vertexShaderCode.append(line);
            vertexShaderCode.append("\n");
        }

        // 加载着色器文件代码
        while ((line = fragmentBufferedReader.readLine()) != null) {
            fragmentShaderCode.append(line);
            fragmentShaderCode.append("\n");
        }

        // 创建并返回ProgramInfo对象
        return new ProgramInfo(vertexShaderCode.toString(), fragmentShaderCode.toString());
    }

    public void createProgram() {
        vertexShader = OpenGLTools.createShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShader = OpenGLTools.createShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        programHandle = OpenGLTools.createProgram(vertexShader, fragmentShader);
        GLES20.glLinkProgram(programHandle);
        initShaderHandle();
    }

    public void useProgram() {
        GLES20.glUseProgram(programHandle);
    }


    private void initShaderHandle() {
        //获取句柄
        positionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition");
        normalHandle = GLES20.glGetAttribLocation(programHandle, "aNormal");
        MVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
        ModelMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMMatrix");
        lightLocationHandle = GLES20.glGetUniformLocation(programHandle, "uLightLocation");
        textureHandle = GLES20.glGetAttribLocation(programHandle, "aTextureCoordination");
        cameraLocationHandle = GLES20.glGetUniformLocation(programHandle, "uCamera");
        kdColorHandle = GLES20.glGetUniformLocation(programHandle,"aKdColor");
        kaHandle = GLES20.glGetUniformLocation(programHandle, "ka");
        kdHandle = GLES20.glGetUniformLocation(programHandle, "kd");
        ksHandle = GLES20.glGetUniformLocation(programHandle, "ks");
    }

    public void enableAllVariableHandle() {
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glEnableVertexAttribArray(MVPMatrixHandle);
        GLES20.glEnableVertexAttribArray(ModelMatrixHandle);
        GLES20.glEnableVertexAttribArray(lightLocationHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glEnableVertexAttribArray(cameraLocationHandle);
        GLES20.glEnableVertexAttribArray(kdColorHandle);
        GLES20.glEnableVertexAttribArray(kaHandle);
        GLES20.glEnableVertexAttribArray(kdHandle);
        GLES20.glEnableVertexAttribArray(ksHandle);
    }

    public void disableAllVariableHandle() {
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(MVPMatrixHandle);
        GLES20.glDisableVertexAttribArray(ModelMatrixHandle);
        GLES20.glDisableVertexAttribArray(lightLocationHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
        GLES20.glDisableVertexAttribArray(cameraLocationHandle);
        GLES20.glDisableVertexAttribArray(kdColorHandle);
        GLES20.glDisableVertexAttribArray(kaHandle);
        GLES20.glDisableVertexAttribArray(kdHandle);
        GLES20.glDisableVertexAttribArray(ksHandle);
    }


    public int getProgramHandle() {
        return programHandle;
    }

    public int getVertexShader() {
        return vertexShader;
    }

    public int getFragmentShader() {
        return fragmentShader;
    }

    public String getVertexShaderCode() {
        return vertexShaderCode;
    }

    public String getFragmentShaderCode() {
        return fragmentShaderCode;
    }

    public int getPositionHandle() {
        return positionHandle;
    }

    public int getNormalHandle() {
        return normalHandle;
    }

    public int getMVPMatrixHandle() {
        return MVPMatrixHandle;
    }

    public int getModelMatrixHandle() {
        return ModelMatrixHandle;
    }

    public int getLightLocationHandle() {
        return lightLocationHandle;
    }

    public int getTextureHandle() {
        return textureHandle;
    }

    public int getCameraLocationHandle() {
        return cameraLocationHandle;
    }

    public int getKdColorHandle() {
        return kdColorHandle;
    }

    public int getKaHandle() {
        return kaHandle;
    }

    public int getKdHandle() {
        return kdHandle;
    }

    public int getKsHandle() {
        return ksHandle;
    }
}
