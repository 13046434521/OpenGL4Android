package com.jtl.opengl.camera;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.opengl.Constant;
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
    private byte[] mYData;
    private byte[] mUVData;
    private int width = Constant.WIDTH;
    private int height = Constant.HEIGHT;
    private ByteBuffer mYBuffer;
    private ByteBuffer mUVBuffer;
    private BackGroundNV12RenderNew mBackGroundNV12RenderNew;

    public CameraGLSurface(Context context) {
        super(context);
    }

    public CameraGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mYData = new byte[width * height];
        mYBuffer = ByteBuffer.allocateDirect(width * height);
        mYBuffer.order(ByteOrder.nativeOrder());
        mYBuffer.position(0);

        mUVData = new byte[width * height / 2];
        mUVBuffer = ByteBuffer.allocateDirect(width * height / 2);
        mUVBuffer.order(ByteOrder.nativeOrder());
        mUVBuffer.position(0);
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
//        this.width = width;
//        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        mBackGroundNV12RenderNew.drawFrame(mYBuffer, mUVBuffer, width, height);
    }

    public void setCameraData(byte[] data) {
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, mYData, 0, mYData.length);
            System.arraycopy(data, mYData.length, mUVData, 0, data.length - mYData.length);
            mYBuffer.put(mYData).position(0);
            mUVBuffer.put(mUVData).position(0);
        }
    }

    public void setCameraData(ByteBuffer yData, ByteBuffer uvData) {
        yData.get(mYData, 0, yData.limit());
        uvData.get(mUVData, 0, uvData.limit());

        mYBuffer.position(0);
        mUVBuffer.position(0);
        mYBuffer.put(yData).position(0);
        mUVBuffer.put(uvData).position(0);
    }
}
