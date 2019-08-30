package com.jtl.opengl.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 14:51
 * 描述:GLSurfaceView 的基础类
 * 更改:
 */
public class BaseGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
    public BaseGLSurface(Context context) {
        this(context,null);
    }

    public BaseGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        this.setPreserveEGLContextOnPause(true); // GLSurfaceView  onPause和onResume切换时，是否保留EGLContext上下文
        this.setEGLContextClientVersion(2); //OpenGL ES 的版本
        this.setEGLConfigChooser(8,8,8,8,24,0); //深度位数，在setRender之前调用

        this.setRenderer(this);
        this.setRenderMode(RENDERMODE_CONTINUOUSLY);//RENDERMODE_CONTINUOUSLY 或者 RENDERMODE_WHEN_DIRTY
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1,1,1,0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
