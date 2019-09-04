package com.jtl.opengl.skybox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.helper.ShaderHelper;
import com.socks.library.KLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/3 16:15
 * 描述:天空盒
 * 更改:
 */
public class SkyBoxRender extends BaseRender {
    private static final String TAG = SkyBoxRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/skybox_vert.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/skybox_frag.glsl";
    //    private static final String[] bitmapFilePath = new String[]{"model/sahara_lf.jpg", "model/sahara_rt.jpg",
//            "model/sahara_dn.jpg", "model/sahara_up.jpg"
//            , "model/sahara_ft.jpg", "model/sahara_bk.jpg"};
//    private static final String[] bitmapFilePath = new String[]{"model/left.jpg", "model/right.jpg",
//            "model/bottom.jpg", "model/top.jpg"
//            , "model/front.jpg", "model/back.jpg"};
    private static final String[] bitmapFilePath = new String[]{"model/left.png", "model/right.png",
            "model/bottom.png", "model/top.png"
            , "model/front.png", "model/back.png"};
    private int mProgram;
    private int[] textureId = new int[1];
    private int a_Position;
    private int u_MvpMatrix;
    private int u_TextureUnit;

    //顶点坐标
    private float[] vertexCoord = new float[]{
            -1f, 1f, 1f,    //(0) Top-left near
            1f, 1f, 1f,     //(1) Top-right near
            -1f, -1f, 1f,   //(2) Bottom-left near
            1f, -1f, 1f,    //(3) Bottom-right near
            -1f, 1f, -1f,   //(4) Top-left far
            1f, 1f, -1f,    //(5) Top-right far
            -1f, -1f, -1f,  //(6) Bottom-left far
            1f, -1f, -1f,   //(7) Bottom-right far
    };

//        private float[] vertexCoord = new float[]{
//            -0.5f, 0.5f, 0.5f,    //(0) Top-left near
//            0.5f, 0.5f, 0.5f,     //(0.5) Top-right near
//            -0.5f, -0.5f, 0.5f,   //(2) Bottom-left near
//            0.5f, -0.5f, 0.5f,    //(3) Bottom-right near
//            -0.5f, 0.5f, -0.5f,   //(4) Top-left far
//            0.5f, 0.5f, -0.5f,    //(5) Top-right far
//            -0.5f, -0.5f, -0.5f,  //(6) Bottom-left far
//            0.5f, -0.5f, -0.5f,   //(7) Bottom-right far
//    };
//    private byte[] index = new byte[]{
//            1, 3, 0,
//            0, 3, 2,
//            1, 5, 3,
//            3, 5, 7,
//            7, 5, 6,
//            6, 5, 4,
//            4, 0, 6,
//            6, 0, 2,
//            1, 0, 4,
//            1, 4, 5,
//            7, 6, 3,
//            3, 6, 2,
//    };

    // 立方体索引
    private byte[] index = new byte[]{
            // Front
            1, 3, 0,
            0, 3, 2,

            // Back
            4, 6, 5,
            5, 6, 7,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer mVertexCoord;
    private ByteBuffer mIndexBuffer;

    private Bitmap[] mBitmaps = new Bitmap[6];


    @Override
    protected void createdGLThread(Context context) {
        initProgram(context);
        initData(context);
        initTexture();
    }

    @Override
    protected void onSurfaceChanged(float width, float height) {
        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f,
                0f, 1.0f, 0.0f);

        // 设置透视矩阵
        float ratio = width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 45, ratio, 1f, 100f);
    }

    private void initProgram(Context context) {
        mProgram = GLES20.glCreateProgram();

        int vertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int fragmentShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        a_Position = GLES20.glGetAttribLocation(mProgram, "a_Position");
        u_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "u_MvpMatrix");
        u_TextureUnit = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glDetachShader(mProgram, vertexShader);
        GLES20.glDetachShader(mProgram, fragmentShader);

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        ShaderHelper.checkGLError(TAG, "initProgram");
    }

    private void initData(Context context) {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(mvpMatrix, 0);

        try {
            for (int i = 0; i < mBitmaps.length; ++i) {
                mBitmaps[i] = BitmapFactory.decodeStream(context.getAssets().open(bitmapFilePath[i]));
            }

            ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
            vertexBuffer.order(ByteOrder.nativeOrder());
            mVertexCoord = vertexBuffer.asFloatBuffer();
            mVertexCoord.put(vertexCoord);
            mVertexCoord.position(0);

            mIndexBuffer = ByteBuffer.allocateDirect(index.length * 2);
            mIndexBuffer.order(ByteOrder.nativeOrder());
            mIndexBuffer.put(index);
            mIndexBuffer.position(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTexture() {
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, mBitmaps[0], 0); // 左
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, mBitmaps[1], 0); // 右
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, mBitmaps[2], 0); // 下
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, mBitmaps[3], 0); // 上
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, mBitmaps[4], 0); // 前
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, mBitmaps[5], 0); // 后

        GLES20.glUniform1i(u_TextureUnit, 0);
        for (Bitmap bitmap : mBitmaps) {
            bitmap.recycle();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        ShaderHelper.checkGLError(TAG, "initTexture");
    }

    @Override
    protected void onUpdate(float[] rotateMatrix) {
        // MVP矩阵
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f,
                0f, 1f, 0f);
        KLog.v(TAG, "rotateMatrix:" + Arrays.toString(rotateMatrix) + "\n"
                + "viewMatrix:" + Arrays.toString(viewMatrix) + "\n");

        Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, rotateMatrix, 0);
        KLog.d(TAG, "rotateMatrix:" + Arrays.toString(rotateMatrix) + "\n"
                + "viewMatrix:" + Arrays.toString(viewMatrix) + "\n");

        Matrix.rotateM(viewMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        KLog.w(TAG, "rotateMatrix:" + Arrays.toString(rotateMatrix) + "\n"
                + "viewMatrix:" + Arrays.toString(viewMatrix) + "\n"
                + "projectionMatrix:" + Arrays.toString(projectionMatrix) + "\n"
                + "mvpMatrix:" + Arrays.toString(mvpMatrix) + "\n");

    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);
        GLES20.glUniform1i(u_TextureUnit, 0);
        GLES20.glUniformMatrix4fv(u_MvpMatrix, 1, false, mvpMatrix, 0);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glVertexAttribPointer(a_Position, 3, GLES20.GL_FLOAT, false, 0, mVertexCoord);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

        GLES20.glEnableVertexAttribArray(a_Position);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
        GLES20.glUseProgram(0);

        ShaderHelper.checkGLError(TAG, "onDraw");
    }
}
