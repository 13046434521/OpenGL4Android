package com.jtl.opengl.texture;

import android.os.Bundle;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;

public class TextureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_texture);

        mToolbar.setTitle(R.string.activity_model);
    }
}
