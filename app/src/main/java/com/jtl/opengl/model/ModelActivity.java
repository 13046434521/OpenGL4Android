package com.jtl.opengl.model;

import android.os.Bundle;
import android.widget.SeekBar;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;
import com.socks.library.KLog;

import androidx.appcompat.widget.AppCompatSeekBar;

public class ModelActivity extends BaseActivity implements AppCompatSeekBar.OnSeekBarChangeListener{
    private static final String TAG=ModelActivity.class.getSimpleName();
    private ModelGLSurface mModelGLSurface;
    private AppCompatSeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_model);

        mModelGLSurface=findViewById(R.id.gl_model_surface);
        mSeekBar = findViewById(R.id.sb_model_scale);
        mSeekBar.setOnSeekBarChangeListener(this);

        mToolbar.setTitle(R.string.activity_model);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        KLog.e(TAG,progress+"  fromUser:"+fromUser);
//        mModelGLSurface.setModelScale(progress/100f);
        mModelGLSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                mModelGLSurface.setModelScale(progress/100f);
            }
        });

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        KLog.e(TAG,"onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        KLog.e(TAG,"onStopTrackingTouch");
    }
}
