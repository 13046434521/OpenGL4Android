package com.jtl.opengl.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.jtl.opengl.R;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mToolbar = findViewById(R.id.tool_base_menu);
        mFrameLayout = findViewById(R.id.frame_base_layout);
    }


    public void setContentView1(@LayoutRes int res) {
        View view = View.inflate(this, res, null);
        mFrameLayout.addView(view);
    }
}
