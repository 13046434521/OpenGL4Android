package com.jtl.opengl.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/9 21:32
 * 描述:
 * 更改:
 */
public class CameraWrapper implements ImageReader.OnImageAvailableListener {
    private static final String TAG = CameraWrapper.class.getSimpleName();
    private Context mContext;
    private String mCameraId;
    private int mWidth;
    private int mHeight;
    private boolean isAutoFocus;
    private byte[] mImageData = null;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mColorCaptureSession;
    private ImageReader mColorImageReader;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private CameraDataListener mCameraDataListener;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    // 彩色相机打开关闭锁
    private Semaphore mCameraSemaphore = new Semaphore(1);

    public CameraWrapper(Context context, String cameraId, int width, int height, boolean isAutoFocus, CameraDataListener cameraDataListener) {
        mContext = context;
        mCameraId = cameraId;
        mWidth = width;
        mHeight = height;
        this.isAutoFocus = isAutoFocus;
        mCameraDataListener = cameraDataListener;
    }

    @SuppressLint("MissingPermission")
    public void openCamera() {
        startBackgroundThread();

        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                return;
            }

            manager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraSemaphore.release();
                    mCameraDevice = camera;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    mCameraSemaphore.release();
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    mCameraSemaphore.release();
                    camera.close();
                    mCameraDevice = null;
                }
            }, mBackgroundHandler);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        mColorImageReader = ImageReader.newInstance(mWidth, mHeight, ImageFormat.YUV_420_888, /*maxImages*/2);
        mColorImageReader.setOnImageAvailableListener(this, mBackgroundHandler);
        List<Surface> surfaceList = new ArrayList<Surface>();
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Surface imageReaderSurface = mColorImageReader.getSurface();

        Surface surface = null;
        mPreviewRequestBuilder.addTarget(imageReaderSurface);

        if (surface == null) {
            surfaceList = Arrays.asList(imageReaderSurface);
        } else {
            surfaceList = Arrays.asList(surface, imageReaderSurface);
        }

        try {
            mCameraDevice.createCaptureSession(surfaceList,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mCameraDevice) {
                                return;
                            }

                            mColorCaptureSession = cameraCaptureSession;
                            try {
                                if (isAutoFocus) {
                                    // 自动对焦
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                } else {
                                    // 固定焦距
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                                    mPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 0f);
                                }
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mColorCaptureSession.setRepeatingRequest(mPreviewRequest, null, mBackgroundHandler);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {
            mCameraSemaphore.acquire();
            if (null != mColorCaptureSession) {
                mColorCaptureSession.close();
                mColorCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mColorImageReader) {
                mColorImageReader.close();
                mColorImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock color camera closing.", e);
        } finally {
            mCameraSemaphore.release();
            mImageData = null;

            stopBackgroundThread();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (reader == null) {
            return;
        }
        Image image = reader.acquireLatestImage();
        if (image == null) {
            return;
        }

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();

        yBuffer.position(0);
        uvBuffer.position(0);
////
//        // 从字节数组缓存池中取用字节数组，如果为null则重新生成
//        byte[] imageData =null;
//        if (imageData == null){
////            Log.d("ReuseByte", "创建yuv数组");
//            // 系数1.5是由于预览格式为YUV420
//            KLog.w(TAG,yBuffer.limit()+"---"+uvBuffer.limit()+"---"+(uvBuffer.limit()+yBuffer.limit()));
//            KLog.d(TAG,mWidth+"---"+mHeight+"---"+mWidth*mHeight*1.5);
//            imageData = new byte[yBuffer.limit()+uvBuffer.limit()];
//        }
//
//        // 填充Y数据
//        yBuffer.get(imageData, 0, yBuffer.limit());
//        // 偏移Y的长度填充UV数据
//        uvBuffer.get(imageData, yBuffer.limit(), uvBuffer.limit());
//        mCameraDataListener.setCameraDataListener(imageData,image.getTimestamp(),image.getFormat());
        mCameraDataListener.setCameraDataListener(yBuffer, uvBuffer, image.getTimestamp(), image.getFormat());
        image.close();
    }

    public interface CameraDataListener {

        /**
         * @param imageData
         * @param timestamp
         * @param imageFormat
         * @see android.graphics.ImageFormat
         */
        void setCameraDataListener(byte[] imageData, float timestamp, int imageFormat);

        void setCameraDataListener(ByteBuffer yData, ByteBuffer uvData, float timestamp, int imageFormat);
    }
}
