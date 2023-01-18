package com.shenyutao.opengldemo.bean;

import android.view.animation.Animation;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjModelTest extends TestCase {

    public void testLoadObjModel() throws FileNotFoundException {
        File file = new File("f://j20.obj");
        if (!file.exists()) {
            return;
        }
        List<ObjModel> objModelList = ObjModel.loadObjModel(new FileInputStream(file), null);
        for (ObjModel objModel : objModelList) {
            System.out.println("Ka: " + Arrays.toString(objModel.getKa()));
            System.out.println("Kd: " + Arrays.toString(objModel.getKd()));
            System.out.println("Ks: " + Arrays.toString(objModel.getKs()));
            System.out.println("vertex: " + Arrays.toString(objModel.getVertex()));
            System.out.println("texture: " + Arrays.toString(objModel.getTexture()));
            System.out.println("normal: " + Arrays.toString(objModel.getNormal()));
        }
    }
}