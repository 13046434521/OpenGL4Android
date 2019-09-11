package com.jtl.opengl.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;
import com.jtl.opengl.helper.PermissionHelper;
import com.socks.library.KLog;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import static com.jtl.opengl.Constant.HEIGHT;
import static com.jtl.opengl.Constant.WIDTH;

public class CameraActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, CameraWrapper.CameraDataListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private CameraManager mCameraManager;
    private CameraGLSurface mCameraGLSurface;
    private String mCameraId;
    private CameraWrapper mCameraWrapper;
    private int width = WIDTH;
    private int height = HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_camera);
        mCameraGLSurface = findViewById(R.id.gl_camera_surface);

        mToolbar.setTitle(R.string.activity_camera);
        mToolbar.inflateMenu(R.menu.camera_menu);
        mToolbar.setOnMenuItemClickListener(this);
        initPermission();
    }

    //获取权限
    private void initPermission() {
        if (!PermissionHelper.hasCameraPermission(this) || !PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            PermissionHelper.requestCameraPermission(this);
        } else {
            init();
        }
    }

    private void init() {
        try {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : mCameraManager.getCameraIdList()) {
                Menu menu = mToolbar.getMenu();
                menu.add(cameraId);
                mCameraId = cameraId;
            }
            for (Size size : CameraWrapper.getSizes(this, mCameraId)) {
                Menu menu = mToolbar.getMenu();
                menu.add(size.getWidth() + "x" + size.getHeight());
            }

//            if (mCameraWrapper == null) {
//                mCameraWrapper = new CameraWrapper(this, mCameraId, width, height, true, this);
//                mCameraWrapper.openCamera();
//            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraWrapper != null) {
            mCameraWrapper.openCamera();
        }
        if (mCameraGLSurface != null) {
            mCameraGLSurface.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraGLSurface != null) {
            mCameraGLSurface.onPause();
        }
        if (mCameraWrapper != null) {
            mCameraWrapper.closeCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            initPermission();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String title = (String) item.getTitle();
//        switch (title){
//            case "0":
//                showToast("后置");
//                break;
//            case "1":
//                showToast("前置");
//                break;
//        }
        if (mCameraWrapper == null) {
            mCameraWrapper = new CameraWrapper(this, title, width, height, true, this);
            mCameraWrapper.openCamera();
        }
        return true;
    }

    @Override
    public void setCameraDataListener(final byte[] imageData, float timestamp, int imageFormat) {
        mCameraGLSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraGLSurface.setCameraData(imageData);
            }
        });
    }

    @Override
    public void setCameraDataListener(final ByteBuffer yData, final ByteBuffer uvData, float timestamp, int imageFormat) {
        KLog.v(TAG, Thread.currentThread().getName());
        mCameraGLSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                KLog.i(TAG, Thread.currentThread().getName());
                mCameraGLSurface.setCameraData(yData, uvData);
            }
        });
    }

    @Override
    public void setCameraDataListener(final ByteBuffer yData, final ByteBuffer uData, final ByteBuffer vData, float timestamp, int imageFormat) {
        KLog.v(TAG, Thread.currentThread().getName());
        mCameraGLSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                KLog.i(TAG, Thread.currentThread().getName());
                mCameraGLSurface.setCameraData(yData, uData, vData);
            }
        });
    }
}
