package com.shenyutao.opengldemo.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.shenyutao.opengldemo.tool.BitmapUtil;
import com.shenyutao.opengldemo.tool.ListTools;
import com.shenyutao.opengldemo.tool.OpenGLTools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ObjModel {
    private static final int BIT_PER_FLOAT = 4;
    private static final int BIT_PER_SHORT = 2;

    private final Context context;
    private final String folder;

    private float[] vertex;
    private float[] texture;
    private float[] normal;
    private float[] color = new float[]{0f, 0f, 0f, 1f};

    private List<Float> vertexList;
    private List<Float> textureList;
    private List<Float> normalList;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;

    private MtlInfo mtlInfo;
    private Bitmap bitmap;
    private float alpha;
    private int textureId = 0;
    private float diffuse[];

    private float[] ka = {1f, 1f, 1f, 1f};
    private float[] kd = {1f, 1f, 1f, 1f};
    private float[] ks = {1f, 1f, 1f, 1f};
    private FloatBuffer kaBuffer;
    private FloatBuffer kdBuffer;
    private FloatBuffer ksBuffer;

    public void initObjModel() {
        if (mtlInfo != null) {
            if (folder.equals("")) {
                bitmap = BitmapUtil.getBitmapFromAsset(context, mtlInfo.Kd_Texture);
                Log.i("TAG", "initObjModel: " + folder + "/" + mtlInfo.Kd_Texture);
            } else {
                bitmap = BitmapUtil.getBitmapFromAsset(context, folder + "/" + mtlInfo.Kd_Texture);
                Log.i("TAG", "initObjModel: " + folder + "/" + mtlInfo.Kd_Texture);
            }

            alpha = mtlInfo.alpha;
            diffuse = mtlInfo.Kd_Color;
            color = new float[]{diffuse[0], diffuse[1], diffuse[2], alpha};
            textureId = OpenGLTools.getTextureIdByBitmap(bitmap);
            if (mtlInfo.Ka_Color != null) {
                ka = new float[]{mtlInfo.Ka_Color[0], mtlInfo.Ka_Color[1], mtlInfo.Ka_Color[2], alpha};
            }
            if (mtlInfo.Kd_Color != null) {
                kd = new float[]{mtlInfo.Kd_Color[0], mtlInfo.Kd_Color[1], mtlInfo.Kd_Color[2], alpha};
            }
            if (mtlInfo.Ks_Color != null) {
                ks = new float[]{mtlInfo.Ks_Color[0], mtlInfo.Ks_Color[1], mtlInfo.Ks_Color[2], alpha};
            }

        }
        initBuffer();
    }

    private void initBuffer() {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertex).position(0);

        textureBuffer = ByteBuffer.allocateDirect(texture.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(texture).position(0);

        normalBuffer = ByteBuffer.allocateDirect(normal.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalBuffer.put(normal).position(0);


        colorBuffer = ByteBuffer.allocateDirect(color.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(color).position(0);

        kaBuffer = ByteBuffer.allocateDirect(ka.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        kaBuffer.put(ka).position(0);

        kdBuffer = ByteBuffer.allocateDirect(kd.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        kdBuffer.put(kd).position(0);

        ksBuffer = ByteBuffer.allocateDirect(ks.length * BIT_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        ksBuffer.put(ks).position(0);
    }

    public static List<ObjModel> loadObjModelFromAssets(String objFileName, Context context, String folder) {
        try {
            return loadObjModel(context.getAssets().open(objFileName), context, folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ObjModel> loadObjModelFromFile(String objFilePath, Context context, String folder) throws FileNotFoundException {
        try {
            return loadObjModel(new FileInputStream(objFilePath), context, folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ObjModel> loadObjModel(InputStream inputStream, Context context, String folder) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        List<ObjModel> objModel2List = new ArrayList<>();
        ObjModel curObjModel = new ObjModel(context, folder);
        MtlInfo curMtlInfo = null;

        String l = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            List<Float> vertexList = new ArrayList<>();
            List<Float> vertexResult = new ArrayList<>();
            List<Float> textureList = new ArrayList<>();
            List<Float> textureResult = new ArrayList<>();
            List<Float> normalList = new ArrayList<>();
            List<Float> normalResult = new ArrayList<>();

            Map<String, MtlInfo> mtlMap = new HashMap<>();

            // ????????????????????????USE_MTL?????????f???????????????????????????objInfo
            while ((line = bufferedReader.readLine()) != null) {
                // ?????? ???????????????
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }

                l = line;

                StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                int tokenCount = stringTokenizer.countTokens();
                if (tokenCount == 0) {
                    continue;
                }

                String type = stringTokenizer.nextToken();
                type = type.trim();

                switch (type) {
                    case MTL_LIB:
                        String mtlName = stringTokenizer.nextToken();
                        mtlMap = MtlInfo.load(mtlName, folder, context.getResources());
                        break;
                    case USE_MTL:
                        // ?????????????????????
                        String curMtlName = stringTokenizer.nextToken();
                        curMtlInfo = mtlMap.getOrDefault(curMtlName, null);
                        // ????????????Obj????????????????????????????????????????????????
                        if (vertexResult.size() != 0) {
                            curObjModel.setVertex(ListTools.floatListToArray(vertexResult));
                            curObjModel.setTexture(ListTools.floatListToArray(textureResult));
                            curObjModel.setNormal(ListTools.floatListToArray(normalResult));
                            objModel2List.add(curObjModel);
                            // ???????????????
                            curObjModel = new ObjModel(context, folder);
                            curObjModel.setMtlInfo(curMtlInfo);
                            vertexResult = new ArrayList<>();
                            textureResult = new ArrayList<>();
                            normalResult = new ArrayList<>();
                        } else {
                            curObjModel.setMtlInfo(curMtlInfo);
                        }
                        break;
                    case V:
                        vertexList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        vertexList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        vertexList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        break;
                    case VT:
                        textureList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        textureList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        break;
                    case VN:
                        normalList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        normalList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        normalList.add(Float.parseFloat(stringTokenizer.nextToken()));
                        break;
                    case F:
                        /*
                         * obj???F???????????????????????????????????????????????????f????????????????????? / ????????????
                         * faces[i]???????????????????????????" v / vt / vn "
                         * */
                        String[] faces = new String[tokenCount - 1];
                        for (int i = 0; i < faces.length; i++) {
                            faces[i] = stringTokenizer.nextToken();
                        }
                        /*
                         * ??????????????????i??????????????????
                         * ??????????????????j????????????????????????????????????????????????2
                         * */
                        for (int i = 1; i < faces.length - 2; i++) {
                            // ???????????????????????????????????????????????????????????????????????????vertexResult,textureResult,normalResult?????????????????????
                            addDataToResultList(new StringTokenizer(faces[0], "/"), vertexList, textureList, normalList, vertexResult, textureResult, normalResult);
                            for (int j = 0; j < 2; j++) {
                                String tokenBeOption;
                                tokenBeOption = faces[i + j];
                                // ???faces[i] ??? / ????????????????????????
                                StringTokenizer fs = new StringTokenizer(tokenBeOption, "/");
                                // ????????????????????????????????????????????????????????????????????????vertexResult,textureResult,normalResult?????????????????????
                                addDataToResultList(fs, vertexList, textureList, normalList, vertexResult, textureResult, normalResult);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            // ???????????????Obj????????????????????????????????????????????????
            if (vertexResult.size() != 0) {
                curObjModel.setMtlInfo(curMtlInfo);
                curObjModel.setVertex(ListTools.floatListToArray(vertexResult));
                curObjModel.setTexture(ListTools.floatListToArray(textureResult));
                curObjModel.setNormal(ListTools.floatListToArray(normalResult));
                objModel2List.add(curObjModel);
            }
        } catch (Exception e) {
            Log.i("ERROR", "loadObjModel: " + e + " " + l);
        }

        return objModel2List;
    }

    private static void addDataToResultList(StringTokenizer fs, List<Float> vertexList, List<Float> textureList, List<Float> normalList,
                                            List<Float> vertexResult, List<Float> textureResult, List<Float> normalResult) {
        if (fs.countTokens() == 3) {
            // ?????????????????????????????????
            int vIndex = Integer.parseInt(fs.nextToken()) - 1;
            // ?????????????????????????????????
            int vtIndex = Integer.parseInt(fs.nextToken()) - 1;
            // ??????????????????????????????
            int vnIndex = Integer.parseInt(fs.nextToken()) - 1;

            // ??????????????????????????????
            vertexResult.add(vertexList.get(vIndex * 3));
            vertexResult.add(vertexList.get(vIndex * 3 + 1));
            vertexResult.add(vertexList.get(vIndex * 3 + 2));

            // ??????????????????????????????
            textureResult.add(textureList.get(vtIndex * 2));
            textureResult.add(1f - textureList.get(vtIndex * 2 + 1));

            // ????????????????????????????????????
            normalResult.add(normalList.get(vnIndex * 3));
            normalResult.add(normalList.get(vnIndex * 3 + 1));
            normalResult.add(normalList.get(vnIndex * 3 + 2));
        }
        // ??????????????????????????? v//vn
        if (fs.countTokens() == 2) {
            int vIndex = Integer.parseInt(fs.nextToken()) - 1;
            int vnIndex = Integer.parseInt(fs.nextToken()) - 1;

            vertexResult.add(vertexList.get(vIndex * 3));
            vertexResult.add(vertexList.get(vIndex * 3 + 1));
            vertexResult.add(vertexList.get(vIndex * 3 + 2));

            textureResult.add(0f);
            textureResult.add(0f);

            normalResult.add(normalList.get(vnIndex * 3));
            normalResult.add(normalList.get(vnIndex * 3 + 1));
            normalResult.add(normalList.get(vnIndex * 3 + 2));
        }
    }


    /**
     * obj???????????????
     */
    // obj?????????????????????
    private static final String MTL_LIB = "mtllib";
    // ?????????
    private static final String G = "g";
    // o ????????????(Object name)
    private static final String O = "o";
    // ??????
    private static final String V = "v";
    // ????????????
    private static final String VT = "vt";
    // ????????????
    private static final String VN = "vn";
    // ???????????????
    private static final String USE_MTL = "usemtl";
    // v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(???????????????1)
    private static final String F = "f";

    public float[] getVertex() {
        return vertex;
    }

    public void setVertex(float[] vertex) {
        this.vertex = vertex;
    }

    public float[] getTexture() {
        return texture;
    }

    public void setTexture(float[] texture) {
        this.texture = texture;
    }

    public float[] getNormal() {
        return normal;
    }

    public void setNormal(float[] normal) {
        this.normal = normal;
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public void setVertexBuffer(FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public void setTextureBuffer(FloatBuffer textureBuffer) {
        this.textureBuffer = textureBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public void setNormalBuffer(FloatBuffer normalBuffer) {
        this.normalBuffer = normalBuffer;
    }

    public void setMtlInfo(MtlInfo mtlInfo) {
        this.mtlInfo = mtlInfo;
    }

    public MtlInfo getMtlInfo() {
        return mtlInfo;
    }

    public List<Float> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<Float> vertexList) {
        this.vertexList = vertexList;
    }

    public List<Float> getTextureList() {
        return textureList;
    }

    public void setTextureList(List<Float> textureList) {
        this.textureList = textureList;
    }

    public List<Float> getNormalList() {
        return normalList;
    }

    public void setNormalList(List<Float> normalList) {
        this.normalList = normalList;
    }

    public ObjModel(Context context, String folder) {
        this.context = context;
        this.folder = folder;
    }

    public int getTextureId() {
        return textureId;
    }

    public FloatBuffer getColorBuffer() {
        return colorBuffer;
    }

    public float[] getColor() {
        return color;
    }

    public boolean hasTexture() {
        return bitmap != null;
    }

    public float[] getKa() {
        return ka;
    }

    public float[] getKd() {
        return kd;
    }

    public float[] getKs() {
        return ks;
    }

    public FloatBuffer getKaBuffer() {
        return kaBuffer;
    }

    public FloatBuffer getKdBuffer() {
        return kdBuffer;
    }

    public FloatBuffer getKsBuffer() {
        return ksBuffer;
    }

    @Override
    public String toString() {
        return "ObjModel{" +
                "context=" + context +
                ", vertex=" + Arrays.toString(vertex) +
                ", texture=" + Arrays.toString(texture) +
                ", normal=" + Arrays.toString(normal) +
                ", color=" + Arrays.toString(color) +
                ", vertexList=" + vertexList +
                ", textureList=" + textureList +
                ", normalList=" + normalList +
                ", vertexBuffer=" + vertexBuffer +
                ", textureBuffer=" + textureBuffer +
                ", normalBuffer=" + normalBuffer +
                ", colorBuffer=" + colorBuffer +
                ", mtlInfo=" + mtlInfo +
                ", bitmap=" + bitmap +
                ", alpha=" + alpha +
                ", textureId=" + textureId +
                ", diffuse=" + Arrays.toString(diffuse) +
                ", ka=" + Arrays.toString(ka) +
                ", kd=" + Arrays.toString(kd) +
                ", ks=" + Arrays.toString(ks) +
                ", kaBuffer=" + kaBuffer +
                ", kdBuffer=" + kdBuffer +
                ", ksBuffer=" + ksBuffer +
                '}';
    }
}
