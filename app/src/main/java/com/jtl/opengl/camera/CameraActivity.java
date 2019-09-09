package com.jtl.opengl.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;
import com.jtl.opengl.helper.PermissionHelper;
import com.socks.library.KLog;

import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

public class CameraActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, CameraWrapper.CameraDataListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private CameraManager mCameraManager;
    private CameraGLSurface mCameraGLSurface;
    private TextView mTextView;
    private String mCameraId;
    private CameraWrapper mCameraWrapper;
    private int width = 1080;
    private int height = 1920;
    private volatile byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_camera);
        mCameraGLSurface = findViewById(R.id.gl_camera_surface);
        mTextView = findViewById(R.id.tv_camera_test);

        mCameraGLSurface.post(new Runnable() {
            @Override
            public void run() {
                width = mCameraGLSurface.getWidth();
                height = mCameraGLSurface.getHeight();
                KLog.w(TAG, width + "---" + height);
            }
        });

        mToolbar.setTitle(R.string.activity_camera);
        mToolbar.inflateMenu(R.menu.camera_menu);
        mToolbar.setOnMenuItemClickListener(this);
        initPermission();
    }

    //获取权限
    private void initPermission() {
        if (!PermissionHelper.hasCameraPermission(this)) {
            PermissionHelper.requestCameraPermission(this);
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
            if (mCameraWrapper == null) {
                KLog.w(TAG, width + "---" + height);
                mCameraWrapper = new CameraWrapper(this, "0", width, height, true, this);
            }
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
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            initPermission();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        return true;
    }

    @Override
    public void setCameraDataListener(final byte[] imageData, float timestamp, int imageFormat) {
//        if (!mConcurrentLinkedQueue.isEmpty()){
//            mConcurrentLinkedQueue.clear();
//        }
//        mConcurrentLinkedQueue.add(imageData);
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
}
