package com.jtl.opengl.skybox;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;

import com.jtl.opengl.R;

import androidx.appcompat.app.AppCompatActivity;

public class SkyBoxActivity extends AppCompatActivity implements SensorEventListener {
    private SkyBoxGLSurface mSkyBoxGLSurface;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private float[] mRotateMatrix = new float[16];
    private float[] mTranslateMatrix = new float[16];
    private Point mPoint;
    private float eventX = 0;
    private float eventY = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (mSkyBoxGLSurface != null) {
            mSkyBoxGLSurface.onPause();
        }
        mSensorManager.unregisterListener(this, mRotationSensor);
    }

    private float aa = 0;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_box);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        mPoint = new Point();
        defaultDisplay.getSize(mPoint);
        Matrix.setIdentityM(mRotateMatrix, 0);
        Matrix.setIdentityM(mTranslateMatrix, 0);
        mSkyBoxGLSurface = findViewById(R.id.gl_sky_surface);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);//旋转矢量传感器
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSkyBoxGLSurface != null) {
            mSkyBoxGLSurface.onResume();
        }
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mRotateMatrix, event.values);
//            mSkyBoxGLSurface.setRotateMatrix(mRotateMatrix);
            mSkyBoxGLSurface.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mSkyBoxGLSurface.setRotateMatrix(mRotateMatrix);
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        float[] rotate = new float[16];
//        Matrix.setIdentityM(mRotateMatrix, 0);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            eventX = event.getX();
            eventY = event.getY();
            Matrix.rotateM(mRotateMatrix, 0, 10, 1f, 1f, 1f);
//            Matrix.rotateM(mRotateMatrix,0,45,0f,1f,0f);
//            mRotateMatrix =rotate;
            mSkyBoxGLSurface.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mSkyBoxGLSurface.setRotateMatrix(mRotateMatrix);
                }
            });
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Matrix.translateM(mTranslateMatrix, 0, (event.getX() - eventX) / mPoint.x, -(event.getY() - eventY) / mPoint.y, -10f);
            eventX = event.getX();
            eventY = event.getY();
//            mRotateMatrix =rotate;
            mSkyBoxGLSurface.queueEvent(new Runnable() {
                @Override
                public void run() {
//                    mSkyBoxGLSurface.setRotateMatrix(mTranslateMatrix);
                }
            });
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            eventX = 0;
            eventY = 0;

//            Matrix.setIdentityM(mTranslateMatrix,0);
//            Matrix.setIdentityM(mRotateMatrix,0);
        }


        return true;
    }
}
