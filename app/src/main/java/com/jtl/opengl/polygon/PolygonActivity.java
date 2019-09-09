package com.jtl.opengl.polygon;

import android.os.Bundle;
import android.view.MenuItem;

import com.jtl.opengl.Constant;
import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;

import androidx.appcompat.widget.Toolbar;

import static com.jtl.opengl.Constant.TRIANGLE;

public class PolygonActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {
    private PolygonGLSurface mPolygonGLSurface;
    private @Constant.Polygon
    int mType = TRIANGLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_polygon);
        mPolygonGLSurface = findViewById(R.id.gl_polygon_surface);

        mToolbar.setTitle(R.string.activity_polygon);
        mToolbar.inflateMenu(R.menu.polygon_menu);
        mToolbar.setOnMenuItemClickListener(this);
        mPolygonGLSurface.setType(mType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPolygonGLSurface != null) {
            mPolygonGLSurface.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPolygonGLSurface != null) {
            mPolygonGLSurface.onPause();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.polygon_menu_triangle:
                mType = TRIANGLE;
                break;
            case R.id.polygon_menu_cube:
                mType = Constant.CUBE;
                break;
            default:
                break;
        }
        //切换类型
        mPolygonGLSurface.setType(mType);
        return true;
    }
}
