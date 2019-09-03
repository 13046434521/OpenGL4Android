package com.jtl.opengl.skybox;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.jtl.opengl.R;

import androidx.appcompat.app.AppCompatActivity;

public class SkyBoxActivity extends AppCompatActivity implements SensorEventListener {
    private SkyBoxGLSurface mSkyBoxGLSurface;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private float[] mRotateMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_box);

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
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSkyBoxGLSurface != null) {
            mSkyBoxGLSurface.onPause();
        }
        mSensorManager.unregisterListener(this, mRotationSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mRotateMatrix, event.values);
            mSkyBoxGLSurface.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mSkyBoxGLSurface.setRotateMatrix(mRotateMatrix);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
