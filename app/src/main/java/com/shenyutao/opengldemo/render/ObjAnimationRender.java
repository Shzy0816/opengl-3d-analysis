package com.shenyutao.opengldemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.shenyutao.opengldemo.bean.MatrixState;
import com.shenyutao.opengldemo.bean.ObjModel;
import com.shenyutao.opengldemo.bean.ProgramInfo;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ObjAnimationRender implements GLSurfaceView.Renderer {
    private final List<ObjModel> objModelList;
    private final Context context;

    /**
     * 用于描述顶点变换矩阵，相机位置，光源位置的类
     */
    private MatrixState matrixState;

    /**
     * 程序类
     */
    ProgramInfo programInfo;

    public ObjAnimationRender(List<ObjModel> objModelList, Context context) {
        this.objModelList = objModelList;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // 初始化ProgramInfo，包括加载sh文件
        programInfo = ProgramInfo.loadProgramFromAssets("vertex.sh", "frag.sh", context);
        programInfo.createProgram();
        programInfo.useProgram();

        // 初始化每一个ObjModel
        for (ObjModel objModel2 : objModelList) {
            objModel2.initObjModel();
        }

        // 初始化ViewMatrix
        float cameraX = 0.0f;
        float cameraY = 50f;
        float cameraZ = -100.0f;
        float lookAtX = 0.0f;
        float lookAtY = 0.0f;
        float lookAtZ = 0.0f;
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        matrixState.setViewMatrix(cameraX, cameraY, cameraZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
        matrixState.setLightLocation(move,0f,0f);
        for (ObjModel objModel : objModelList) {
            Log.i("Test", "onSurfaceCreated: " + objModel.getMtlInfo().Kd_Texture);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    float angleMercury = 0f;
    float angleVenus = 0f;
    float angleEarth = 0f;
    float angleMars = 0f;
    float angleJupiter = 0f;
    float angleSaturn = 0f;
    float angleUranus = 0f;
    float angleNeptune = 0f;
    float move = 40f;

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清理屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 开启变量传输
        programInfo.enableAllVariableHandle();
        // 绘制所有Obj
        angleMercury += 0.5f;
        angleVenus += 0.49f;
        angleEarth += 0.48f;
        angleMars += 0.47f;
        angleJupiter += 0.46f;
        angleSaturn += 0.45f;
        angleUranus += 0.44f;
        angleNeptune += 0.43f;

        angleMercury = angleMercury >= 360f ? angleMercury - 360f : angleMercury;
        angleVenus = angleVenus >= 360f ? angleVenus - 360f : angleVenus;
        angleEarth = angleEarth >= 360f ? angleEarth - 360f : angleEarth;
        angleMars = angleMars >= 360f ? angleMars - 360f : angleMars;
        angleJupiter = angleJupiter >= 360f ? angleJupiter - 360f : angleJupiter;
        angleSaturn = angleSaturn >= 360f ? angleSaturn - 360f : angleSaturn;
        angleUranus = angleUranus >= 360f ? angleUranus - 360f : angleUranus;
        angleNeptune = angleNeptune >= 360f ? angleNeptune - 360f : angleNeptune;


        // 太阳
        matrixState.setCenterMotionModelMatrix(20f + move, 0f, 0f,
                0f, 0f, 0f,
                0f, 0f, 0f);
        drawObjModel(objModelList.get(6));

        // 水星
        matrixState.setCenterMotionModelMatrix(-10f + move, 0f, 0f,
                angleMercury, 0f, 0f,
                -10f, 0f, 0f);
        drawObjModel(objModelList.get(2));

        // 金星
        matrixState.setCenterMotionModelMatrix(-15f + move, 0f, 0f,
                angleVenus, 0f, 0f,
                -25f, 0f, 0f);
        drawObjModel(objModelList.get(8));

        // 地球
        matrixState.setCenterMotionModelMatrix(-18f + move, 0f, 0f,
                angleEarth, 0f, 0f,
                -40f, 0f, 0f);
        drawObjModel(objModelList.get(9));

        // 火星
        matrixState.setCenterMotionModelMatrix(-21f + move, 0f, 0f,
                angleMars, 0f, 0f,
                -55f, 0f, 0f);
        drawObjModel(objModelList.get(1));

        // 木星
        matrixState.setCenterMotionModelMatrix(-24f + move, 0f, 0f,
                angleJupiter, 0f, 0f,
                -70f, 0f, 0f);
        drawObjModel(objModelList.get(0));

        // 土星
        matrixState.setCenterMotionModelMatrix(-24f + move, 0f, 0f,
                angleSaturn, 0f, 0f,
                -91f, 0f, 0f);
        drawObjModel(objModelList.get(4));
        drawObjModel(objModelList.get(5));

        // 天王星
        matrixState.setCenterMotionModelMatrix(-29f + move, 0f, 0f,
                angleUranus, 0f, 0f,
                -110f, 0f, 0f);
        drawObjModel(objModelList.get(7));


        // 海王星
        matrixState.setCenterMotionModelMatrix(-37f + move, 0f, 0f,
                angleNeptune, 0f, 0f,
                -130f, 0f, 0f);
        drawObjModel(objModelList.get(3));

//        matrixState.setCenterMotionModelMatrix(20f, 0f, 0f,
//                angle, 0f, 0f,
//                20f, 0f, 0f);
//        drawObjModel(objModelList.get(0));
//        matrixState.setCenterMotionModelMatrix(0f, 0f, 0f,
//                angle, 0f, 0f,
//                0f,0f,0f);
//        drawObjModel(objModelList.get(1));
//        matrixState.setCenterMotionModelMatrix(0f, 0f, 0f,
//                angle, 0f, 0f,
//                0f,0f,0f);
//        drawObjModel(objModelList.get(2));
//        matrixState.setCenterMotionModelMatrix(0f, 0f, 0f,
//                angle, 0f, 0f,
//                0f,0f,0f);
//        drawObjModel(objModelList.get(3));
//        drawObjModel(objModelList.get(6));

        // 关闭变量传输
        programInfo.disableAllVariableHandle();
    }

    private void drawObjModel(ObjModel objModel) {
        //传入顶点
        GLES20.glVertexAttribPointer(programInfo.getPositionHandle(), 3, GLES20.GL_FLOAT, false, 12, objModel.getVertexBuffer());
        //传入纹理
        GLES20.glVertexAttribPointer(programInfo.getTextureHandle(), 2, GLES20.GL_FLOAT, false, 2 * 4, objModel.getTextureBuffer());
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, objModel.getTextureId());
        //绑定物体漫反射光
        GLES20.glUniform4fv(programInfo.getKdColorHandle(), 1, objModel.getColorBuffer());
        //ka
        GLES20.glUniform4fv(programInfo.getKaHandle(), 1, objModel.getKaBuffer());
        //kd
        GLES20.glUniform4fv(programInfo.getKdHandle(), 1, objModel.getKdBuffer());
        //ks
        GLES20.glUniform4fv(programInfo.getKsHandle(), 1, objModel.getKsBuffer());
        //传入顶点法向量
        GLES20.glVertexAttribPointer(programInfo.getNormalHandle(), 3, GLES20.GL_FLOAT, false, 3 * 4, objModel.getNormalBuffer());
        //将总矩阵放入着色器
        GLES20.glUniformMatrix4fv(programInfo.getMVPMatrixHandle(), 1, false, matrixState.getMVPMatrix(), 0);
        //将控制平移、旋转、缩放变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(programInfo.getModelMatrixHandle(), 1, false, matrixState.getModelMatrix(), 0);
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(programInfo.getCameraLocationHandle(), 1, matrixState.getCameraLocationBuffer());
        //将光源位置传入着色器
        GLES20.glUniform3fv(programInfo.getLightLocationHandle(), 1, matrixState.getLightLocationBuffer());
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, objModel.getVertex().length / 3);
    }

    public MatrixState getMatrixState() {
        return matrixState;
    }

    public void setMatrixState(MatrixState matrixState) {
        this.matrixState = matrixState;
    }
}
