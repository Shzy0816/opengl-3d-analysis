package com.shenyutao.opengldemo.tool;

import java.util.List;

public class ListTools {
    public static float[] floatListToArray(List<Float> list){
        float[] arrays = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arrays[i] = list.get(i);
        }
        return arrays;
    }
}
