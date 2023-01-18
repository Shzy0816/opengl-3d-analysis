package com.shenyutao.opengldemo.tool;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class OpenGLTools {
    public static int  createProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        return program;
    }

    public static int createShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        return shader;
    }

    //从sh脚本中加载shader内容的方法
    public static String loadShaderCodeFromAssets(String fileName, Resources r) {
        String result = null;
        try {
            InputStream in = r.getAssets().open(fileName);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * bitmap 生成 textureId
     *
     * @param bitmap
     * @return
     */
    public static int getTextureIdByBitmap(Bitmap bitmap) {
        // 生成纹理ID
        int[] textures = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            GLES20.glGenTextures(1, // 产生的纹理id的数量
                    textures, // 纹理id的数组
                    0 // 偏移量
            );
            int textureId = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);

            // 实际加载纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型，在OpenGLES中必须为GL10.GL_TEXTURE_2D
                    0, // 纹理的层次，0表示基本图像层，可以理解为直接贴图
                    bitmap, // 纹理图像
                    0 // 纹理边框尺寸
            );
            bitmap.recycle(); // 纹理加载成功后释放图片
            return textureId;
        }
        return 0;
    }

}
