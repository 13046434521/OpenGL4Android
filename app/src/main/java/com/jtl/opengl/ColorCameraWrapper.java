package com.jtl.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

class ColorCameraWrapper implements ImageReader.OnImageAvailableListener {
    private Context context;
    private String cameraId;

    private CameraCaptureSession mColorCaptureSession;
    private CameraDevice mColorCameraDevice;
    private ImageReader mColorImageReader;
    private CaptureRequest.Builder mColorPreviewRequestBuilder;
    private CaptureRequest mColorPreviewRequest;

    private int mWidth;
    private int mHeight;
    private TextureView textureView;

//    private Handler colorHandler;

    private byte[] mImageData = null;
//    private HandlerThread handlerThread;

    private Handler cameraHandler;

    private boolean autoFoucs = true;
    // 彩色相机打开关闭锁
    private Semaphore colorCameraSemaphore = new Semaphore(1);
    private long firstFrameTime = 0;
    private long currentFrameTime = 0;

    //    private void startBackgroundHandler(){
//        handlerThread = new HandlerThread("DepthThread");
//        handlerThread.start();
//        colorHandler = new Handler(handlerThread.getLooper());
//    }
    private int frameCount = 0;


    public ColorCameraWrapper(Context context, String cameraId, int width, int height, TextureView textureView, Handler cameraHandler, boolean autoFocus) {
        this.context = context;
        this.cameraId = cameraId;

        this.mWidth = width;
        this.mHeight = height;

        this.textureView = textureView;

        this.cameraHandler = cameraHandler;

        this.autoFoucs = autoFocus;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void openCamera() {

//        startBackgroundHandler();

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!colorCameraSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                return;
            }
            // 打开彩色图相机
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    colorCameraSemaphore.release();
                    mColorCameraDevice = camera;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    colorCameraSemaphore.release();
                    camera.close();
                    mColorCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    colorCameraSemaphore.release();
                    camera.close();
                    mColorCameraDevice = null;
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        mColorImageReader = ImageReader.newInstance(mWidth, mHeight, ImageFormat.YUV_420_888, /*maxImages*/2);
        mColorImageReader.setOnImageAvailableListener(this, cameraHandler);

        List<Surface> surfaceList = new ArrayList<Surface>();
        try {
            mColorPreviewRequestBuilder = mColorCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Surface imageReaderSurface = mColorImageReader.getSurface();

        Surface surface = null;
        if (textureView != null) {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                return;
            }

            texture.setDefaultBufferSize(mWidth, mHeight);
            surface = new Surface(texture);
        }

        if (surface != null) {
            mColorPreviewRequestBuilder.addTarget(surface);
        }
        mColorPreviewRequestBuilder.addTarget(imageReaderSurface);

        if (surface == null) {
            surfaceList = Arrays.asList(imageReaderSurface);
        } else {
            surfaceList = Arrays.asList(surface, imageReaderSurface);
        }

        try {
            mColorCameraDevice.createCaptureSession(surfaceList,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mColorCameraDevice) {
                                return;
                            }

                            mColorCaptureSession = cameraCaptureSession;
                            try {
                                if (autoFoucs) {
                                    // 自动对焦
                                    mColorPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                } else {
                                    // 固定焦距
                                    mColorPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                                    mColorPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 0f);
                                }
//                                mColorPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                                mColorPreviewRequest = mColorPreviewRequestBuilder.build();
                                mColorCaptureSession.setRepeatingRequest(mColorPreviewRequest, null, cameraHandler);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {
            colorCameraSemaphore.acquire();
            if (null != mColorCaptureSession) {
                mColorCaptureSession.close();
                mColorCaptureSession = null;
            }
            if (null != mColorCameraDevice) {
                mColorCameraDevice.close();
                mColorCameraDevice = null;
            }
            if (null != mColorImageReader) {
                mColorImageReader.close();
                mColorImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock color camera closing.", e);
        } finally {
            colorCameraSemaphore.release();
        }
//        if (handlerThread != null){
//            handlerThread.quitSafely();
//        }

        mImageData = null;
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

//        Log.d("JniHelper", "Java>>>" + image.getTimestamp());
//        if (firstFrameTime == 0){
//            firstFrameTime = System.currentTimeMillis();
//        }
//        currentFrameTime = System.currentTimeMillis();
//        frameCount++;

//        if (currentFrameTime > firstFrameTime){
//            Log.e("JniHelper", "ColorFPS>>" + 1000/((currentFrameTime - firstFrameTime) / frameCount));
//        }

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();

        yBuffer.position(0);
        uvBuffer.position(0);

        // 从字节数组缓存池中取用字节数组，如果为null则重新生成
        byte[] imageData = null;
        if (imageData == null) {
//            Log.d("ReuseByte", "创建yuv数组");
            // 系数1.5是由于预览格式为YUV420
            imageData = new byte[mWidth * mHeight * 3 / 2];
        }

        // 填充Y数据
        yBuffer.get(imageData, 0, yBuffer.limit());
        // 偏移Y的长度填充UV数据
        uvBuffer.get(imageData, yBuffer.limit(), uvBuffer.limit());
        image.close();
    }

    public void updateFocusDistance(int percent) {
        try {
//            Log.d("=====", "调整焦距" + percent);
            mColorPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            mColorPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 10.0f * percent / 100f);
            mColorPreviewRequest = mColorPreviewRequestBuilder.build();
            mColorCaptureSession.setRepeatingRequest(mColorPreviewRequest, null, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
