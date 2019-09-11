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
    private byte[] mUData;
    private byte[] mVData;
    private int width = Constant.WIDTH;
    private int height = Constant.HEIGHT;
    private ByteBuffer mYBuffer;
    private ByteBuffer mUBuffer;
    private ByteBuffer mVBuffer;
    private BackGroundNV12RenderNew mBackGroundNV12RenderNew;
    private CameraRender mCameraRender;
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

        mUData = new byte[width * height / 2];
        mUBuffer = ByteBuffer.allocateDirect(width * height / 2);
        mUBuffer.order(ByteOrder.nativeOrder());
        mUBuffer.position(0);

        mVData = new byte[width * height / 2];
        mVBuffer = ByteBuffer.allocateDirect(width * height / 2);
        mVBuffer.order(ByteOrder.nativeOrder());
        mVBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mBackGroundNV12RenderNew = new BackGroundNV12RenderNew();
        mBackGroundNV12RenderNew.createOnGLThread(getContext().getApplicationContext());

        mCameraRender = new CameraRender();
        mCameraRender.createdGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
//        this.width = width;
//        this.height = height;

        mCameraRender.onSurfaceChanged(this.width, this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
//        mBackGroundNV12RenderNew.drawFrame(mYBuffer, mUBuffer, width, height);
        mCameraRender.onDraw(mYBuffer, mUBuffer, mVBuffer);
    }

    public void setCameraData(byte[] data) {
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, mYData, 0, mYData.length);
            System.arraycopy(data, mYData.length, mUData, 0, mUData.length);
            System.arraycopy(data, mYData.length + mUData.length, mVData, 0, mVData.length);
//            FileHelper.getInstance().createFileWithByte(data,FileHelper.getInstance().getDataFolderPath(),System.currentTimeMillis()+"jtl.yuv");

            mYBuffer.put(mYData).position(0);
            mUBuffer.put(mUData).position(0);
            mVBuffer.put(mVData).position(0);
        }
    }

    public void setCameraData(ByteBuffer yData, ByteBuffer uvData) {
        yData.get(mYData, 0, yData.limit());
        uvData.get(mUData, 0, uvData.limit());

        mYBuffer.position(0);
        mUBuffer.position(0);
        mYBuffer.put(yData).position(0);
        mUBuffer.put(uvData).position(0);
    }

    public void setCameraData(ByteBuffer yData, ByteBuffer uData, ByteBuffer vData) {
        mYBuffer.put(yData).position(0);
        mYBuffer.put(uData).position(0);
        mYBuffer.put(vData).position(0);
    }
}
