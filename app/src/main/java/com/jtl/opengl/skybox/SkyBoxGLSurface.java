package com.jtl.opengl.skybox;

import android.content.Context;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.jtl.opengl.base.BaseGLSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/3 17:19
 * 描述:
 * 更改:
 */
public class SkyBoxGLSurface extends BaseGLSurface {
    private SkyBoxRender mSkyBoxRender;
    private volatile float[] rotateMatrix = new float[16];

    public SkyBoxGLSurface(Context context) {
        super(context);
    }

    public SkyBoxGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mSkyBoxRender = new SkyBoxRender();
        mSkyBoxRender.createdGLThread(getContext().getApplicationContext());

        Matrix.setIdentityM(rotateMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        mSkyBoxRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        mSkyBoxRender.onUpdate(rotateMatrix);
        mSkyBoxRender.onDraw();
    }

    public void setRotateMatrix(float[] rotateMatrix) {
        this.rotateMatrix = rotateMatrix;
    }
}
