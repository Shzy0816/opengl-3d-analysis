package com.shenyutao.opengldemo.callback;

import com.shenyutao.opengldemo.bean.ObjModel;

import java.util.List;

public interface ObjLoadCallback {
    void loadStart();
    void loadEnd(List<ObjModel> objModelList);
}
