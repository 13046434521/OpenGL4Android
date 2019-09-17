package com.jtl.opengl.model;

import java.util.Arrays;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/16 16:06
 * 描述:
 * 更改:
 */
public class ModelMtl {
    public static final String newmtl = "newmtl";
    public static final String Ka = "Ka";     //阴影色
    public static final String Kd = "Kd";     //固有色
    public static final String Ks = "Ks";     //高光色
    public static final String Ke = "Ke";     //
    public static final String Ns = "Ns";                    //shininess
    public static final String map_Kd = "map_Kd";               //固有纹理贴图
    public static final String map_Ks = "map_Ks";               //高光纹理贴图
    public static final String map_Ka = "map_Ka";               //阴影纹理贴图
    public static final String illum = "illum";
    private String newmtl_Data;
    private float[] KaData = new float[3];     //阴影色
    private float[] KdData = new float[3];     //固有色
    private float[] KsData = new float[3];     //高光色
    private float[] KeData = new float[3];     //
    private float NsData;                    //shininess
    private String map_Kd_Data;               //固有纹理贴图
    private String map_Ks_Data;               //高光纹理贴图
    private String map_Ka_Data;               //阴影纹理贴图
    //denotes the illumination model used by the material.
    // illum = 1 indicates a flat material with no specular highlights,
    // so the value of Ks is not used.
    // illum = 2 denotes the presence of specular highlights,
    // and so a specification for Ks is required.
    private int illumData;

    public String getNewmtl_Data() {
        return newmtl_Data;
    }

    public void setNewmtl_Data(String newmtl_Data) {
        this.newmtl_Data = newmtl_Data;
    }

    public float[] getKaData() {
        return KaData;
    }

    public void setKaData(float[] kaData) {
        KaData = kaData;
    }

    public float[] getKdData() {
        return KdData;
    }

    public void setKdData(float[] kdData) {
        KdData = kdData;
    }

    public float[] getKsData() {
        return KsData;
    }

    public void setKsData(float[] ksData) {
        KsData = ksData;
    }

    public float[] getKeData() {
        return KeData;
    }

    public void setKeData(float[] keData) {
        KeData = keData;
    }

    public float getNsData() {
        return NsData;
    }

    public void setNsData(float nsData) {
        NsData = nsData;
    }

    public String getMap_Kd_Data() {
        return map_Kd_Data;
    }

    public void setMap_Kd_Data(String map_Kd_Data) {
        this.map_Kd_Data = map_Kd_Data;
    }

    public String getMap_Ks_Data() {
        return map_Ks_Data;
    }

    public void setMap_Ks_Data(String map_Ks_Data) {
        this.map_Ks_Data = map_Ks_Data;
    }

    public String getMap_Ka_Data() {
        return map_Ka_Data;
    }

    public void setMap_Ka_Data(String map_Ka_Data) {
        this.map_Ka_Data = map_Ka_Data;
    }

    public int getIllumData() {
        return illumData;
    }

    public void setIllumData(int illumData) {
        this.illumData = illumData;
    }

    @Override
    public String toString() {
        return "ModelMtl{" +
                "newmtl_Data='" + newmtl_Data + '\'' +
                ", KaData=" + Arrays.toString(KaData) +
                ", KdData=" + Arrays.toString(KdData) +
                ", KsData=" + Arrays.toString(KsData) +
                ", KeData=" + Arrays.toString(KeData) +
                ", NsData=" + NsData +
                ", map_Kd_Data='" + map_Kd_Data + '\'' +
                ", map_Ks_Data='" + map_Ks_Data + '\'' +
                ", map_Ka_Data='" + map_Ka_Data + '\'' +
                ", illumData=" + illumData +
                '}';
    }
}
