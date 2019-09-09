package com.jtl.opengl.base;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jtl.opengl.R;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    private FrameLayout mFrameLayout;
    protected Point mPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mToolbar = findViewById(R.id.tool_base_menu);
        mFrameLayout = findViewById(R.id.frame_base_layout);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        mPoint = new Point();
        defaultDisplay.getSize(mPoint);
    }


    public void setContentView1(@LayoutRes int res) {
        View view = View.inflate(this, res, null);
        mFrameLayout.addView(view);
    }

    protected void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
