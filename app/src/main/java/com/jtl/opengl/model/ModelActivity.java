package com.jtl.opengl.model;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jtl.opengl.Constant;
import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;
import com.socks.library.KLog;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

public class ModelActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, AppCompatSeekBar.OnSeekBarChangeListener{
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

        initMenu();
    }

    private void initMenu() {
        mToolbar.setTitle(R.string.activity_model);
        mToolbar.setOnMenuItemClickListener(this);
        Menu menu = mToolbar.getMenu();
        menu.add(Constant.MODEL_PIKACHU);
        menu.add(Constant.MODEL_Umbreon_High_Poly);
        menu.add(Constant.MODEL_CORONA);
        menu.add(Constant.MODEL_NANOSUIT);
        menu.add(Constant.MODEL_3D);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String modelName;
        switch (item.getTitle().toString()){
            case Constant.MODEL_PIKACHU:
                modelName=Constant.MODEL_PIKACHU;
                break;
            case Constant.MODEL_CORONA:
                modelName=Constant.MODEL_CORONA;
                break;
            case Constant.MODEL_Umbreon_High_Poly:
                modelName=Constant.MODEL_Umbreon_High_Poly;
                break;
            case Constant.MODEL_NANOSUIT:
                modelName=Constant.MODEL_NANOSUIT;
                break;
            default:
                modelName=Constant.MODEL_3D;
                break;
        }

        Toast.makeText(this,modelName,Toast.LENGTH_SHORT).show();
        mModelGLSurface.initData(modelName);

        return true;
    }
}
