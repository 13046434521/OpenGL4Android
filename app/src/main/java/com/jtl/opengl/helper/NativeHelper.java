package com.jtl.opengl.helper;

/**
 * @author :jtl
 * 日期:Created in 2019/9/27 11:38
 * 描述:
 * 更改:
 */
public class NativeHelper {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void yuvToRgb(byte[] yuvData,byte[] rgbData,int width,int height);
}
