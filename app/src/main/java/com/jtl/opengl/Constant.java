package com.jtl.opengl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IntDef;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/9 16:31
 * 描述:常量类
 * 更改:
 */
public class Constant {
    public static final int TRIANGLE = 0;
    public static final int CUBE = 1;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @IntDef({TRIANGLE, CUBE})
    public @interface Polygon {
    }

//    public static final int WIDTH=1920;
//    public static final int HEIGHT=1080;

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
}
