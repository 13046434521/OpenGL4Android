package com.jtl.opengl.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import androidx.annotation.IntDef;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/16 14:27
 * 描述:
 * 更改:
 */
public class ModelObj {
    public static final int TRIANGLE = 0;
    public static final int QUADRILATERAL = 1;
    public static final String mtllib = "mtllib";
    public static final String v = "v";
    public static final String g = "g";
    public static final String vt = "vt";
    public static final String vn = "vn";
    public static final String f = "f";
    public static final String usemtl = "usemtl";
    public @Shape
    int shape = TRIANGLE;
    public ModelMtl mModelMtl;
    private ArrayList<Float> mVertexList;
    private ArrayList<Float> mTextureList;
    private ArrayList<Float> mNormalList;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mNormalBuffer;
    private int vertexCount;
    private int vertexSize;
    private int textureSize;
    private int normalSize;

    public ModelObj() {
        mModelMtl=new ModelMtl();
    }

    public void addVert(float vertex) {
        if (mVertexList == null) {
            mVertexList = new ArrayList<>();
        }

        mVertexList.add(vertex);
    }

    public void addTexture(float vertex) {
        if (mTextureList == null) {
            mTextureList = new ArrayList<>();
        }

        mTextureList.add(vertex);
    }

    public void addNormal(float noramal) {
        if (mNormalList == null) {
            mNormalList = new ArrayList<>();
        }

        mNormalList.add(noramal);
    }

    public void setVertex(ArrayList<Float> vertexList) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexList.size() * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();


        for (float v : vertexList) {
            mVertexBuffer.put(v);
        }
        mVertexBuffer.position(0);
        vertexSize = vertexList.size();
        vertexCount = vertexList.size() / 3;// 三个为一个顶点，计算共有多个顶点
    }

    public void setTexture(ArrayList<Float> textureList) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(textureList.size() * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer.asFloatBuffer();


        for (float v : textureList) {
            mTextureBuffer.put(v);
        }
        textureSize = textureList.size();
        mTextureBuffer.position(0);
    }

    public void setNormal(ArrayList<Float> normalList) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(normalList.size() * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mNormalBuffer = byteBuffer.asFloatBuffer();


        for (float v : normalList) {
            mNormalBuffer.put(v);
        }
        normalSize = normalList.size();
        mNormalBuffer.position(0);
    }

    public void setModelData() {
        if (mVertexList != null) {
            setVertex(mVertexList);
            mVertexList.clear();
            mVertexList = null;
        }
        if (mTextureList != null) {
            setTexture(mTextureList);
            mTextureList.clear();
            mTextureList = null;
        }
        if (mNormalList != null) {
            setNormal(mNormalList);
            mNormalList.clear();
            mNormalList = null;
        }
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return mTextureBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return mNormalBuffer;
    }

    public void setModelMtl(ModelMtl modelMtl) {
        mModelMtl = modelMtl;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public String toString() {
        return "ModelObj{" +
                "mModelMtl=" + mModelMtl.toString() +
                ", vertexCount=" + vertexCount +
                ", vertexSize=" + vertexSize +
                ", textureSize=" + textureSize +
                ", normalSize=" + normalSize +
                '}';
    }

    public int getShape() {
        return shape;
    }

    public void setShape(@Shape int shape) {
        this.shape = shape;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @IntDef({TRIANGLE, QUADRILATERAL})
    public @interface Shape {
    }
}
