package com.jtl.opengl.model;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.opengl.base.BaseGLSurface;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/16 10:14
 * 描述:
 * 更改:
 */
public class ModelGLSurface extends BaseGLSurface {
    private static final String TAG=ModelGLSurface.class.getSimpleName();
    private List<ModelObj> mModelObjList;
    private List<ModelRender> mModelRenderList;
    private List<ModelRender1> mModelRender1List;
    public ModelGLSurface(Context context) {
        super(context);
    }
    public ModelGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        mModelObjList =  ModelHelper.getInstance().initModelObj(getContext(),"model/UmbreonHighPoly");
//        mModelObjList =  ModelHelper.getInstance().initModelObj(getContext(),"model/nanosuit");
        mModelRenderList = new ArrayList<>();
        mModelRender1List = new ArrayList<>();
        for (int i=0;i<mModelObjList.size();i++){
            ModelRender modelRender=new ModelRender();
            mModelRenderList.add(modelRender);

            ModelRender1 modelRender1=new ModelRender1();
            mModelRender1List.add(modelRender1);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        for (int i=0;i<mModelRender1List.size();i++){
            ModelRender1 render=mModelRender1List.get(i);
            render.createdGLThread(getContext().getApplicationContext());
            render.initModelObj(mModelObjList.get(i),getContext().getApplicationContext());
        }

        for (int i=0;i<mModelObjList.size();i++){
            ModelRender render=mModelRenderList.get(i);
            render.createdGLThread(getContext().getApplicationContext());
            render.initModelObj(mModelObjList.get(i),getContext().getApplicationContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        for (ModelRender modelRender:mModelRenderList){
            modelRender.onSurfaceChanged(width,height);
        }

        for (ModelRender1 modelRender1:mModelRender1List){
            modelRender1.onSurfaceChanged(width,height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        for (ModelRender modelRender:mModelRenderList){
            modelRender.onDraw();
        }

//        for (ModelRender1 modelRender1:mModelRender1List){
//            modelRender1.onDraw();
//        }
    }

    public void setModelScale(float scale){
        for (int i=0;i<mModelRender1List.size();i++){
            ModelRender render=mModelRenderList.get(i);
            render.setScale(scale);
        }

        for (int i=0;i<mModelObjList.size();i++){
            ModelRender1 render=mModelRender1List.get(i);
            render.setScale(scale);
        }

        KLog.e(TAG,scale);
    }
}
