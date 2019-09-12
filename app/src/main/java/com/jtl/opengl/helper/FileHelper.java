package com.jtl.opengl.helper;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/11 19:31
 * 描述:
 * 更改:
 */
public class FileHelper {
    private static String mSDCardFolderPath;
    private static String mDataFolderPath;
    private static String mImgFolderPath;

    private FileHelper() {
        init();
    }

    public static FileHelper getInstance() {
        return FileHelperHolder.FILE_HELPER;
    }

    private void init() {
        mSDCardFolderPath = getSDCardFolderPath();
        mDataFolderPath = getDataFolderPath();
    }

    public String getDataFolderPath() {
        if (TextUtils.isEmpty(mDataFolderPath)) {
            mDataFolderPath = getSDCardFolderPath() + "Data/";
        }
        mkdirs(mDataFolderPath);

        return mDataFolderPath;
    }

    public String getSDCardFolderPath() {
        if (TextUtils.isEmpty(mSDCardFolderPath)) {
            mSDCardFolderPath = Environment.getExternalStorageDirectory().getPath() + "/OpenGL/";
        }
        mkdirs(mSDCardFolderPath);

        return mSDCardFolderPath;
    }

    public String getImgFolderPath() {
        if (TextUtils.isEmpty(mImgFolderPath)) {
            mImgFolderPath = getSDCardFolderPath() + "Img/";
        }
        mkdirs(mImgFolderPath);

        return mImgFolderPath;
    }

    public void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public int saveBitmap(Bitmap bitmap) {
        //文件名为时间
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(timeStamp));
        String fileName = sd + ".jpg";
        return saveBitmap(FileHelper.getInstance().getImgFolderPath(), fileName, bitmap);
    }

    public int saveBitmap(String path, String fileName, Bitmap bitmap) {
        File file = new File(path + fileName);
        FileOutputStream fileOutputStream = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 1;
    }

    public void createFileWithByte(byte[] bytes, String path, String fileName) {
        File file = new File(path + fileName);
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class FileHelperHolder {
        public static final FileHelper FILE_HELPER = new FileHelper();
    }
}
