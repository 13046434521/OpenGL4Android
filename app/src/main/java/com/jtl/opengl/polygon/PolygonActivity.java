package com.jtl.opengl.polygon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jtl.opengl.R;

public class PolygonActivity extends AppCompatActivity {
    private PolygonGLSurface mPolygonGLSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygon);
        mPolygonGLSurface=findViewById(R.id.gl_polygon_surface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPolygonGLSurface!=null){
            mPolygonGLSurface.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPolygonGLSurface!=null){
            mPolygonGLSurface.onPause();
        }
    }
}
