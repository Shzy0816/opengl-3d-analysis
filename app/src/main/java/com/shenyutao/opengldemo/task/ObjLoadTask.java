package com.shenyutao.opengldemo.task;

import android.content.Context;

import com.shenyutao.opengldemo.bean.ObjModel;
import com.shenyutao.opengldemo.callback.ObjLoadCallback;

import java.util.List;

public class ObjLoadTask implements Runnable {
    private final ObjLoadCallback objLoadCallback;
    private final String objFileName;
    private final String folderPath;
    private final Context context;

    public ObjLoadTask(String folderPath, String objFileName, ObjLoadCallback objLoadCallback, Context context) {
        this.objLoadCallback = objLoadCallback;
        this.objFileName = objFileName;
        this.folderPath = folderPath;
        this.context = context;
    }

    @Override
    public void run() {
        objLoadCallback.loadStart();
        List<ObjModel> objModelList = ObjModel.loadObjModelFromAssets(folderPath + "/" + objFileName, context, folderPath);
        objLoadCallback.loadEnd(objModelList);
    }
}
