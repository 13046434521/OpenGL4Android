package com.jtl.openg.bitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;

import com.jtl.openg.R;

public class BitmapActivity extends AppCompatActivity {
    private BitmapGLSurface mBitmapGLSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);

        mBitmapGLSurface=findViewById(R.id.gl_bitmap_surface);
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
