package com.shenyutao.opengldemo.bean;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author Shzy
 * @description mtl文件信息保存类
 */
public class MtlInfo {
    // 材质对象名称
    public String name;
    // 环境光
    public float[] Ka_Color;
    // 散射光
    public float[] Kd_Color;
    // 镜面光
    public float[] Ks_Color;
    // 高光调整参数
    public float ns;
    // 溶解度，为0时完全透明，1完全不透明
    public float alpha = 1f;
    // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
    public String Ka_Texture;
    public String Kd_Texture;
    public String Ks_ColorTexture;
    public String Ns_Texture;
    public String alphaTexture;
    public String bumpTexture;
    private final String folder;

    public MtlInfo(String folder) {
        this.folder = folder;
    }


    /**
     * 加载材质的方法
     *
     * @param fileName assets的mtl文件名
     * @param folder   assets的mtl所处文件夹
     * @param res
     * @return
     */
    public static HashMap<String, MtlInfo> load(String fileName, String folder, Resources res) throws Exception {
        // 创建一个空的材质列表  <材质名称，材质信息>
        HashMap<String, MtlInfo> mMTLMap = new HashMap<>();

        String filePath;
        if (folder.equals("")) {
            filePath = fileName;
        } else {
            filePath = folder + "/" + fileName;
        }
        // 如果资源不存在或者文件为空，直接返回空的材质列表
        if (res == null || TextUtils.isEmpty(filePath)) {
            return mMTLMap;
        }

        MtlInfo currMtlInfo = null;

        // 读取assets下文件
        try (InputStream inputStream = res.getAssets().open(filePath);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            // 行数据
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // 跳过空行 和 '#'
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }

                // 分割字符串 StringTokenizer 和 String.split方法一样
                StringTokenizer parts = new StringTokenizer(line, " ");
                // 如果分割的个数为0，进入下一次循环
                int numTokens = parts.countTokens();
                if (numTokens == 0) {
                    continue;
                }

                // 取出第一个字符
                String type = parts.nextToken();
                // type = type.replaceAll("\\t", "");
                // type = type.replaceAll(" ", "");
                type = type.trim();
                switch (type) {
                    case NEWMTL:
                        // 定义一个名为 'xxx' 的材质
                        String name = parts.hasMoreTokens() ? parts.nextToken() : "def";
                        // 将上一个对象加入到列表中
                        if (currMtlInfo != null && !mMTLMap.containsKey(name)) {
                            mMTLMap.put(currMtlInfo.name, currMtlInfo);
                        }
                        // 创建材质对象
                        currMtlInfo = new MtlInfo(folder);
                        // 材质对象名称
                        currMtlInfo.name = name;
                        break;
                    case KA:
                        // 环境光
                        currMtlInfo.Ka_Color = getColorFromParts(parts);
                        break;
                    case KD:
                        // 散射光
                        currMtlInfo.Kd_Color = getColorFromParts(parts);
                        break;
                    case KS:
                        // 镜面光
                        currMtlInfo.Ks_Color = getColorFromParts(parts);
                        break;
                    case NS:
                        // 高光调整参数
                        String ns = parts.nextToken();
                        currMtlInfo.ns = Float.parseFloat(ns);
                        break;
                    case D:
                        // 溶解度，为0时完全透明，1完全不透明
                        currMtlInfo.alpha = Float.parseFloat(parts.nextToken());
                        break;
                    case MAP_KA:
                        currMtlInfo.Ka_Texture = parts.nextToken();
                        break;
                    case MAP_KD:
                        currMtlInfo.Kd_Texture = parts.nextToken();
                        break;
                    case MAP_KS:
                        currMtlInfo.Ks_ColorTexture = parts.nextToken();
                        break;
                    case MAP_NS:
                        currMtlInfo.Ns_Texture = parts.nextToken();
                        break;
                    case MAP_D:
                        break;
                    case MAP_TR:
                        currMtlInfo.alphaTexture = parts.nextToken();
                        break;
                    case MAP_BUMP:
                        currMtlInfo.bumpTexture = parts.nextToken();
                        break;
                    default:
                        break;
                }
            }

            if (currMtlInfo != null) {
                mMTLMap.put(currMtlInfo.name, currMtlInfo);
            }

            Log.i("Test", "load: map size" + mMTLMap.size());

        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }

        return mMTLMap;
    }

    //####################################################################################
    /**
     * 材质需解析字段
     */
    // 定义一个名为 'xxx'的材质
    private static final String NEWMTL = "newmtl";
    // 材质的环境光（ambient color）
    private static final String KA = "Ka";
    // 散射光（diffuse color）用Kd
    private static final String KD = "Kd";
    // 镜面光（specular color）用Ks
    private static final String KS = "Ks";
    // 反射指数 定义了反射高光度。该值越高则高光越密集，一般取值范围在0~1000。
    private static final String NS = "Ns";
    // 渐隐指数描述 参数factor表示物体融入背景的数量，取值范围为0.0~1.0，取值为1.0表示完全不透明，取值为0.0时表示完全透明。
    private static final String D = "d";
    // 滤光透射率
    private static final String TR = "Tr";
    // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
    private static final String MAP_KA = "map_Ka";
    private static final String MAP_KD = "map_Kd";
    private static final String MAP_KS = "map_Ks";
    private static final String MAP_NS = "map_Ns";
    private static final String MAP_D = "map_d";
    private static final String MAP_TR = "map_Tr";
    private static final String MAP_BUMP = "map_Bump";

    /**
     * 返回一个oxffffffff格式的颜色值
     *
     * @param parts
     * @return
     */
    private static float[] getColorFromParts(StringTokenizer parts) {
        return new float[]{
                Float.parseFloat(parts.nextToken()),
                Float.parseFloat(parts.nextToken()),
                Float.parseFloat(parts.nextToken())
        };
    }
}

