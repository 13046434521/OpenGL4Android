package com.jtl.opengl.skybox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.helper.ShaderHelper;

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
 * 注意：GL_TEXTURE_CUBE_MAP_POSITIVE_X 右
 *       GL_TEXTURE_CUBE_MAP_NEGATIVE_X 左
 *       GL_TEXTURE_CUBE_MAP_POSITIVE_Y 上
 *       GL_TEXTURE_CUBE_MAP_NEGATIVE_Y 下
 *       GL_TEXTURE_CUBE_MAP_POSITIVE_Z 后
 *       GL_TEXTURE_CUBE_MAP_NEGATIVE_Z 前
 */
public class SkyBoxRender extends BaseRender {
    private static final String TAG = SkyBoxRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/skybox_vert.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/skybox_frag.glsl";
    private static final String[] bitmapFilePath = new String[]{"model/right.jpg", "model/left.jpg",
            "model/top.jpg", "model/bottom.jpg"
            , "model/back.jpg", "model/front.jpg"};

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

    //立方体绘制顺序
    private byte[] index = new byte[]{
            1, 3, 0,
            0, 3, 2,
            1, 5, 3,
            3, 5, 7,
            7, 5, 6,
            6, 5, 4,
            4, 0, 6,
            6, 0, 2,
            1, 0, 4,
            1, 4, 5,
            7, 6, 3,
            3, 6, 2,
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
        initData();
        initTexture(context);
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

    private void initData() {
        //初始化为单位矩阵
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(mvpMatrix, 0);


        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexCoord = vertexBuffer.asFloatBuffer();
        mVertexCoord.put(vertexCoord);
        mVertexCoord.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(index.length * 2);
        mIndexBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);

    }

    private void initTexture(Context context) {
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        try {
            for (int i = 0; i < mBitmaps.length; ++i) {
                mBitmaps[i] = BitmapFactory.decodeStream(context.getAssets().open(bitmapFilePath[i]));
                GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, mBitmaps[i], 0);

                mBitmaps[i].recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        GLES20.glUniform1i(u_TextureUnit, 0);

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


        Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, rotateMatrix, 0);

        Matrix.rotateM(viewMatrix, 0, 90, 1f, 0f, 0f);

        Log.d("Matrix", "\nmViewMatrix:\n" + Arrays.toString(viewMatrix));
        Log.w("Matrix", "\nmProjectionMatrix:\n" + Arrays.toString(projectionMatrix));
        Log.e("Matrix", "\nmMVPMatrix:\n" + Arrays.toString(mvpMatrix));
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);

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
