package com.jtl.opengl.bitmap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.jtl.opengl.R;
import com.jtl.opengl.base.BaseActivity;

public class BitmapActivity extends BaseActivity {
    private BitmapGLSurface mBitmapGLSurface;
    private ImageView mPreviewImage;
    private BitmapPresenter<BitmapActivity> mBitmapPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_bitmap);

        init();
    }

    private void init() {
        mBitmapPresenter = new BitmapPresenter<>(this);

        mBitmapGLSurface = findViewById(R.id.gl_bitmap_surface);
        mPreviewImage = findViewById(R.id.iv_bitmap_preview);
        mToolbar.setTitle(R.string.activity_bitmap);

        addOnClickListener(mPreviewImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bitmap_preview:
                mBitmapPresenter.openAlbum();
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String mImgPath = c.getString(columnIndex);
            showToast(mImgPath);

            mBitmapGLSurface.setBitmap(mImgPath);
            c.close();
        }
    }
}
