package com.jtl.opengl.model;

import android.content.Context;

import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/16 14:10
 * 描述:
 * 更改:
 */
public class ModelHelper {
    private static final String TAG = ModelHelper.class.getSimpleName();

    private ModelHelper() {
    }

    public static ModelHelper getInstance() {
        return ModelHelperHolder.MODEL_HELPER;
    }

    /**
     * @param context
     * @param path    路径:model/pikachu
     * @return
     */
    public List<ModelObj> initModelObj(Context context, String path) {
        ModelObj modelObj = new ModelObj();
        ModelMtl modelMtl = new ModelMtl();
        ArrayList<ModelObj> modelObjData = new ArrayList<>();
        ArrayList<Float> vertex = new ArrayList<Float>();//原始顶点坐标列表
        ArrayList<Float> normal = new ArrayList<Float>();    //原始顶点法线列表
        ArrayList<Float> texture = new ArrayList<Float>();    //原始贴图坐标列表
        HashMap<String, ModelMtl> mtlMap = new HashMap<>();
        HashMap<String, ModelObj> objMap = new HashMap<>();
        try {
            InputStream inputStream = context.getApplicationContext().getAssets().open(path + ".obj");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String content = null;

            while ((content = bufferedReader.readLine()) != null) {
                String[] contents = content.split("[ ]+");
                if (modelObj == null) {
                    KLog.w(TAG, contents[0] + ":" + content);
                }
                if (contents[0].equals(ModelObj.mtllib)) {//读取Mtl相关数据
                    mtlMap = readMtl(context, path);
                } else if (contents[0].equals(ModelObj.usemtl)) {//对应的纹理图片
                    if (mtlMap != null) {
                        modelMtl = mtlMap.get(contents[1]);
                    }

                    if (objMap.containsKey(contents[1])) {
                        modelObj = objMap.get(contents[1]);
                    } else {
                        modelObj = new ModelObj();
                        modelObj.setModelMtl(modelMtl);
                        objMap.put(contents[1], modelObj);
                    }
                    KLog.d(TAG, contents[0] + ":" + content);

                } else if (contents[0].equals(ModelObj.v)) {//将顶点数据放入ArrayList中
                    read(contents, vertex);

                } else if (contents[0].equals(ModelObj.vt)) {//将纹理数据放入ArrayList中
                    read(contents, texture);

                } else if (contents[0].equals(ModelObj.vn)) {
                    //将法线数据放入ArrayList中
                    read(contents, normal);

                } else if (contents[0].equals(ModelObj.f)) {
                    //这里 有的obj是渲染的三角形，有的是四边形，四边形的需要自己在组装一下数据
                    if (contents.length - 1 == 3) {
                        modelObj.setShape(ModelObj.TRIANGLE);
                        //按面的顺序，组装顶点，纹理，以及法线
                        //f 2/58/58 3/59/59 17/60/60 第几个顶点/第几个纹理/第几个法线
                        for (int i = 1; i < contents.length; i++) {
                            String[] fs = contents[i].split("/");
                            if (fs.length > 0) {
                                //v 0.146018 3.220109 2.338209
                                int vertexIndex = Integer.parseInt(fs[0]) - 1;// f 2/58/58 3/59/59 17/60/60 因为f是从1开始，而不是从0开始，所以这里要减1
                                modelObj.addVert(vertex.get(vertexIndex * 3));// 顶点的x值
                                modelObj.addVert(vertex.get(vertexIndex * 3 + 1));// 顶点的y值
                                modelObj.addVert(vertex.get(vertexIndex * 3 + 2));// 顶点的z值
                            }
                            if (fs.length > 1) {
                                //vt 0.637894 0.207629
                                int textureIndex = Integer.parseInt(fs[1]) - 1;
                                modelObj.addTexture(texture.get(textureIndex * 2));
                                modelObj.addTexture(texture.get(textureIndex * 2 + 1));
                            }
                            if (fs.length > 2) {
                                //vn 0.288138 -0.226317 0.930461
                                int normalIndex = Integer.parseInt(fs[2]) - 1;
                                modelObj.addNormal(normal.get(normalIndex * 3));
                                modelObj.addNormal(normal.get(normalIndex * 3 + 1));
                                modelObj.addNormal(normal.get(normalIndex * 3 + 2));
                            }
                        }
                    } else if (contents.length - 1 == 4) {
                        modelObj.setShape(ModelObj.QUADRILATERAL);
                        int [] index=new int[]{1,2,3,1,4,3};
                        for (int i=0;i<index.length;i++){
                            String[] fs = contents[index[i]].split("/");
                            if (fs.length > 0) {
                                //v 0.146018 3.220109 2.338209
                                int vertexIndex = Integer.parseInt(fs[0]) - 1;// f 2/58/58 3/59/59 17/60/60 因为f是从1开始，而不是从0开始，所以这里要减1
                                modelObj.addVert(vertex.get(vertexIndex * 3));// 顶点的x值
                                modelObj.addVert(vertex.get(vertexIndex * 3 + 1));// 顶点的y值
                                modelObj.addVert(vertex.get(vertexIndex * 3 + 2));// 顶点的z值
                            }
                            if (fs.length > 1) {
                                //vt 0.637894 0.207629
                                int textureIndex = Integer.parseInt(fs[1]) - 1;
                                modelObj.addTexture(texture.get(textureIndex * 2));
                                modelObj.addTexture(texture.get(textureIndex * 2 + 1));
                            }
                            if (fs.length > 2) {
                                //vn 0.288138 -0.226317 0.930461
                                int normalIndex = Integer.parseInt(fs[2]) - 1;
                                modelObj.addNormal(normal.get(normalIndex * 3));
                                modelObj.addNormal(normal.get(normalIndex * 3 + 1));
                                modelObj.addNormal(normal.get(normalIndex * 3 + 2));
                            }
                        }
                    }

                } else {
                    KLog.d(TAG, contents[0] + ":" + content);
                }

            }

            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, ModelObj> modelObjEntry : objMap.entrySet()) {
            ModelObj obj = modelObjEntry.getValue();
            obj.setModelData();
            modelObjData.add(obj);
        }

        return modelObjData;
    }

    public HashMap<String, ModelMtl> readMtl(Context context, String path) {
        HashMap<String, ModelMtl> modelMtlMap = new HashMap<>();
        ModelMtl modelMtl = null;
        try {
            InputStream inputStream = context.getResources().getAssets().open(path + ".mtl");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String content = null;
            while ((content = bufferedReader.readLine()) != null) {
                String[] contents = content.split("[ ]+");
                if (contents[0].equals(ModelMtl.newmtl)) {

                    modelMtl = new ModelMtl();
                    modelMtl.setNewmtl_Data(contents[1]);
                    modelMtlMap.put(contents[1], modelMtl);
                } else if (contents[0].trim().equals(ModelMtl.Ka)) {

                    float[] data = read(contents);
                    modelMtl.setKaData(data);
                } else if (contents[0].trim().equals(ModelMtl.Kd)) {

                    float[] data = read(contents);
                    modelMtl.setKdData(data);
                } else if (contents[0].trim().equals(ModelMtl.Ks)) {

                    float[] data = read(contents);
                    modelMtl.setKsData(data);
                } else if (contents[0].trim().equals(ModelMtl.Ke)) {

                    float[] data = read(contents);
                    modelMtl.setKeData(data);
                } else if (contents[0].trim().equals(ModelMtl.Ns)) {

                    modelMtl.setNsData(Float.parseFloat(contents[1]));
                } else if (contents[0].trim().equals(ModelMtl.map_Kd)) {

                    modelMtl.setMap_Kd_Data(contents[1]);
                } else if (contents[0].trim().equals(ModelMtl.map_Ks)) {

                    modelMtl.setMap_Ks_Data(contents[1]);
                } else if (contents[0].trim().equals(ModelMtl.map_Ka)) {

                    modelMtl.setMap_Ka_Data(contents[1]);
                } else if (contents[0].trim().equals(ModelMtl.illum)) {

                    modelMtl.setIllumData(Integer.parseInt(contents[1]));
                }
            }

            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return modelMtlMap;
    }

    private void read(String[] value, ArrayList<Float> list) {
        for (int i = 1; i < value.length; i++) {
            list.add(Float.parseFloat(value[i]));
        }
    }

    private float[] read(String[] value) {
        float[] data = new float[value.length - 1];
        for (int i = 1; i < value.length && i < data.length + 1; i++) {
            data[i - 1] = Float.parseFloat(value[i]);
        }

        return data;
    }

    private static class ModelHelperHolder {
        private static final ModelHelper MODEL_HELPER = new ModelHelper();
    }
}
