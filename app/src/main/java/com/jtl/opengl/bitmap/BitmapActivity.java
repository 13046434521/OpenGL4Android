package com.jtl.opengl.bitmap;

import android.os.Bundle;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;

public class BitmapActivity extends BaseActivity {
    private BitmapGLSurface mBitmapGLSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_bitmap);

        mBitmapGLSurface=findViewById(R.id.gl_bitmap_surface);
        mToolbar.setTitle(R.string.activity_bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBitmapGLSurface!=null){
            mBitmapGLSurface.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBitmapGLSurface!=null){
            mBitmapGLSurface.onPause();
        }
    }
}
