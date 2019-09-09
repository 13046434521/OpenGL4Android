package com.jtl.opengl.camera;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.opengl.base.BaseGLSurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/9 19:44
 * 描述:
 * 更改:
 */
public class CameraGLSurface extends BaseGLSurface {
    private volatile byte[] mData;
    private int width;
    private int height;
    private ByteBuffer mYBuffer;
    private ByteBuffer mUVBuffer;
    private BackGroundNV12RenderNew mBackGroundNV12RenderNew;

    public CameraGLSurface(Context context) {
        super(context);
    }

    public CameraGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mBackGroundNV12RenderNew = new BackGroundNV12RenderNew();
        mBackGroundNV12RenderNew.createOnGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        mBackGroundNV12RenderNew.drawFrame(mYBuffer, mUVBuffer, width, height);
    }

    public void setCameraData(byte[] data) {
        mData = new byte[data.length];
        System.arraycopy(mData, 0, data, 0, data.length);
    }

    public void setCameraData(ByteBuffer yData, ByteBuffer uvData) {
        if (mYBuffer == null) {
            mYBuffer = ByteBuffer.allocateDirect(yData.capacity());
            mYBuffer.order(ByteOrder.nativeOrder());
        }
        if (mUVBuffer == null) {
            mUVBuffer = ByteBuffer.allocateDirect(uvData.capacity());
            mUVBuffer.order(ByteOrder.nativeOrder());
        }
        mYBuffer.position(0);
        mUVBuffer.position(0);
        mYBuffer.put(yData).position(0);
        mUVBuffer.put(uvData).position(0);
    }
}
