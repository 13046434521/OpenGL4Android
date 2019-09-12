package com.jtl.opengl.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jtl.opengl.Constant;
import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;
import com.jtl.opengl.helper.PermissionHelper;

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
        if (mCameraWrapper == null) {
            mCameraWrapper = new CameraWrapper(this, "0", width, height, true, this);
            mCameraWrapper.openCamera();
        }

        initMenu();
    }

    private void initMenu() {
        Menu menu = mToolbar.getMenu();
        menu.add("NV12");
        menu.add("YUV_Y");
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
        if (mCameraGLSurface != null) {
            int type = Constant.YUV420P_NV12;
            switch (item.getTitle().toString()) {
                case "YUV_Y":
                    type = Constant.YUV_Y;
                    break;
                case "NV12":
                    type = Constant.YUV420P_NV12;
                    break;
            }
            mCameraGLSurface.setCameraDataType(type);
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
}
