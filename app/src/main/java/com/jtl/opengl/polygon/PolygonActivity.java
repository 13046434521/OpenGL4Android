package com.jtl.opengl.polygon;

import android.os.Bundle;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;

public class PolygonActivity extends BaseActivity {
    private PolygonGLSurface mPolygonGLSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_polygon);
        mPolygonGLSurface=findViewById(R.id.gl_polygon_surface);

        mToolbar.setTitle(R.string.activity_polygon);
        mToolbar.inflateMenu(R.menu.polygon_menu);
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
