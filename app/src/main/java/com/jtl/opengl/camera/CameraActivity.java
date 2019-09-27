package com.jtl.opengl.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    private CameraGlSurface mCameraGlSurface;
    private ImageView mCameraSwitchImg;
    private ImageView mCameraImg;

    private CameraWrapper mCameraWrapper;
    private CameraPresenter<CameraActivity> mCameraPresenter;

    private @Constant.CameraType
    String mCameraID = Constant.CAMERA_BACK;
    private int width = WIDTH;
    private int height = HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_camera);

        initView();
        initPermission();
    }

    private void initView() {
        mCameraGlSurface = findViewById(R.id.gl_camera_surface);
        mCameraImg = findViewById(R.id.iv_camera_btn);
        mCameraSwitchImg = findViewById(R.id.iv_camera_switch);

        addOnClickListener(mCameraImg, mCameraSwitchImg);
        mToolbar.setTitle(R.string.activity_camera);
        mToolbar.inflateMenu(R.menu.camera_menu);
        mToolbar.setOnMenuItemClickListener(this);
    }

    //获取权限
    private void initPermission() {
        if (!PermissionHelper.hasCameraPermission(this) || !PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            init();
        }
    }

    private void init() {
        mCameraPresenter = new CameraPresenter<>(this);

        if (mCameraWrapper == null) {
            mCameraWrapper = new CameraWrapper(this, width, height, true, this);
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
            mCameraWrapper.openCamera(Constant.CAMERA_BACK);
        }
        if (mCameraGlSurface != null) {
            mCameraGlSurface.onResume();
            mCameraGlSurface.setAspectRatio(height,width);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraGlSurface != null) {
            mCameraGlSurface.onPause();
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
        if (mCameraGlSurface != null) {
            int type = Constant.YUV420P_NV12;
            switch (item.getTitle().toString()) {
                case "YUV_Y":
                    type = Constant.YUV_Y;
                    break;
                case "NV12":
                    type = Constant.YUV420P_NV12;
                    break;
            }
            mCameraGlSurface.setCameraDataType(type);
        }

        return true;
    }

    @Override
    public void setCameraDataListener(@Constant.CameraType final String cameraID, final byte[] imageData, float timestamp, int imageFormat) {
        mCameraGlSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraGlSurface.setCameraData(cameraID, imageData);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera_btn:
                mCameraPresenter.takePhoto(mCameraWrapper.getImageData());
                break;
            case R.id.iv_camera_switch:
                mCameraPresenter.switchCamera(mCameraWrapper);
                mCameraGlSurface.setAspectRatio(height,width);
                break;
            default:
                Toast.makeText(this, "其他", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
