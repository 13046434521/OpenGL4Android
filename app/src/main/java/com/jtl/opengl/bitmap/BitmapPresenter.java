package com.jtl.opengl.bitmap;

import android.content.Intent;

import com.jtl.opengl.base.BasePresenter;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/12 23:14
 * 描述:
 * 更改:
 */
public class BitmapPresenter<T> extends BasePresenter {
    private T mT;

    public BitmapPresenter(T t) {
        mT = t;
    }

    public void openAlbum() {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((BitmapActivity) mT).startActivityForResult(intent, 1);
    }
}
