package com.jtl.opengl.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.jtl.opengl.Constant;
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
public class CameraPresenter<T> implements IPresenter {
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
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(data.length * 2);
        dataBuffer.order(ByteOrder.nativeOrder());
        dataBuffer.put(data).position(0);

        bitmap.copyPixelsFromBuffer(dataBuffer);
        Matrix matrix = new Matrix();
        matrix.setRotate(CAMERA_TYPE == CAMERA_BACK ? -90 : 90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        int result = FileHelper.getInstance().saveBitmap(newBitmap);

        KLog.d(result == 1 ? "保存成功" : "保存失败");
        showToast(result == 1 ? "保存成功" : "保存失败");
    }

    public void showToast(String msg) {
        ((CameraActivity) t).showToast(msg);
    }
}
