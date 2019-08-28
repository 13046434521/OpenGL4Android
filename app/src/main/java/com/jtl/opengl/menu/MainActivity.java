package com.jtl.opengl.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jtl.opengl.R;
import com.jtl.opengl.bitmap.BitmapActivity;
import com.jtl.opengl.polygon.PolygonActivity;
import com.jtl.opengl.texture.TextureActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private MenuAdapter mMenuAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String[] data = new String[]{"多边形", "Bitmap", "纹理"};
    private Class[] classData = new Class[]{PolygonActivity.class, BitmapActivity.class, TextureActivity.class};
    private List<MenuBean> mMenuBeanList = new ArrayList<>(16);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.rv_main_menu);

        for (int i=0;i< data.length ; i++) {
            mMenuBeanList.add(new MenuBean(data[i],classData[i]));
        }

        mMenuAdapter = new MenuAdapter(this, mMenuBeanList, this);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mMenuAdapter);
        mMenuAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                Toast.makeText(this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, v.getId()+"", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
