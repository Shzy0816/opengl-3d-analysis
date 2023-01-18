package com.shenyutao.opengldemo.tool;

import android.content.Context;
import android.os.Environment;

import com.shenyutao.opengldemo.third.CommonUtils;

public class FileTools {
    // 将Assets里面的文件拷贝到外存，方便C层读取
    public static void copyFromAssetsToExternal(Context context) {
        String fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        CommonUtils.copyAssetsDirToSDCard(context, "animation", fileDir + "/model");
        CommonUtils.copyAssetsDirToSDCard(context, "background", fileDir + "/model");
        CommonUtils.copyAssetsDirToSDCard(context, "obj_model", fileDir + "/model");
    }
}
