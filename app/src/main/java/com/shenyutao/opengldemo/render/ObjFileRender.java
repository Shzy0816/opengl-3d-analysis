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
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class ObjFileRender implements GLSurfaceView.Renderer {
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

    public ObjFileRender(List<ObjModel> objModelList, Context context) {
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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清理屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 开启变量传输
        programInfo.enableAllVariableHandle();
        // 绘制所有Obj
        for (ObjModel objModel : objModelList) {
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
        // 关闭变量传输
        programInfo.disableAllVariableHandle();
    }

    public MatrixState getMatrixState() {
        return matrixState;
    }

    public void setMatrixState(MatrixState matrixState) {
        this.matrixState = matrixState;
    }
}
