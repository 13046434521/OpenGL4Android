package com.jtl.opengl.polygon;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.opengl.base.BaseGLSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/28 18:08
 * 描述:
 * 更改:
 */
public class PolygonGLSurface extends BaseGLSurface {
    private TriangleRender mTriangleRender;
    public PolygonGLSurface(Context context) {
        super(context);
    }

    public PolygonGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mTriangleRender=new TriangleRender();
        mTriangleRender.createdGLThread(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        mTriangleRender.onDraw();
    }
}
