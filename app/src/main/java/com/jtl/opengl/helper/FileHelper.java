package com.jtl.opengl.helper;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/11 19:31
 * 描述:
 * 更改:
 */
public class FileHelper {
    private static String mSDCardFolderPath;
    private static String mDataFolderPath;

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

    public void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
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
