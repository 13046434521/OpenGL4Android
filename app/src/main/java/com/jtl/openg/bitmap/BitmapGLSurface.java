package com.jtl.openg.bitmap;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.openg.base.BaseGLSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 15:08
 * 描述:
 * 更改:
 */
public class BitmapGLSurface extends BaseGLSurface {
    private BitmapRender mBitmapRender;
    public BitmapGLSurface(Context context) {
        super(context);
    }

    public BitmapGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        mBitmapRender=new BitmapRender();
        mBitmapRender.createdGLThread(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        mBitmapRender.onDraw();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
