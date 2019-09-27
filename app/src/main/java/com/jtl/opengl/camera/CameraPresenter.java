package com.jtl.opengl.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.jtl.opengl.Constant;
import com.jtl.opengl.base.BasePresenter;
import com.jtl.opengl.helper.FileHelper;
import com.socks.library.KLog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.jtl.opengl.Constant.CAMERA_BACK;
import static com.jtl.opengl.Constant.CAMERA_TYPE;
import static com.jtl.opengl.Constant.HEIGHT;
import static com.jtl.opengl.Constant.WIDTH;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/12 21:09
 * 描述:
 * 更改:
 */
public class CameraPresenter<T> extends BasePresenter implements ICameraPresenter {
    private static final String TAG = CameraPresenter.class.getSimpleName();
    private T t;

    public CameraPresenter(T t) {
        this.t = t;
    }

    public void switchCamera(CameraWrapper cameraWrapper) {
        if (cameraWrapper.getCameraId() == CAMERA_BACK) {
            cameraWrapper.openCamera(Constant.CAMERA_FRONT);
        } else {
            cameraWrapper.openCamera(Constant.CAMERA_BACK);
        }
    }

    public void takePhoto(byte[] data) {
        byte[] rgbData=new byte[data.length* 3];
        YuvToRgb.ConvertYUV2RGB(data,rgbData,WIDTH,HEIGHT);
//        NativeHelper.yuvToRgb(data,rgbData,WIDTH,HEIGHT);
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(rgbData.length);
        dataBuffer.order(ByteOrder.nativeOrder());
        dataBuffer.put(rgbData).position(0);

        bitmap.copyPixelsFromBuffer(dataBuffer);
        Matrix matrix = new Matrix();
        matrix.setRotate(CAMERA_TYPE == CAMERA_BACK ? 90 : -90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        int result = FileHelper.getInstance().saveBitmap(newBitmap);

        bitmap.recycle();
        newBitmap.recycle();
        KLog.d(result == 1 ? "保存成功" : "保存失败");
        showToast(result == 1 ? "保存成功" : "保存失败");
    }

    public void showToast(String msg) {
        ((CameraActivity) t).showToast(msg);
    }
}
