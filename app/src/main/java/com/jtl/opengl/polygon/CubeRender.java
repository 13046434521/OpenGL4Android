package com.jtl.opengl.polygon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.helper.ShaderHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/30 15:18
 * 描述:
 * 更改:
 */
public class CubeRender extends BaseRender {
    private static final String TAG = CubeRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/cube_vertex.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/cube_frag.glsl";
    //    private static final String bitmapFilePath = "model/nba.jpg";
    private static final String[] bitmapFilePath = new String[]{"model/right.jpg", "model/left.jpg",
            "model/top.jpg", "model/bottom.jpg"
            , "model/back.jpg", "model/front.jpg"};
    private Bitmap[] mBitmaps = new Bitmap[6];
    private byte[] index = new byte[]{
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };
    private int mProgram;
    private int[] texture = new int[1];
    private int a_Position;
    private int a_MvpMatrix;
    private int u_TextureUnit;
    private ByteBuffer mBitmapBuffer;
    private FloatBuffer mVertexCoord;
    private ByteBuffer mIndexBuffer;

    private float[] vertexCoord = new float[]{
            -1.0f, 1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f, 1.0f,    //正面左下1
            1.0f, -1.0f, 1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f, 1.0f,    //正面右上3
            -1.0f, 1.0f, -1.0f, 1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f, 1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f, 1.0f,   //反面右下6
            1.0f, 1.0f, -1.0f, 1.0f,    //反面右上7
    };

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    @Override
    protected void createdGLThread(Context context) {
        initProgram(context);
        initData(context);
        initTexture(context);
    }

    @Override
    protected void onSurfaceChanged(float width, float height) {
        //计算宽高比
        float ratio = width / height;
        //设置 ViewMatrix
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //两种设置 ProjectMatrix的方法
//        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        Matrix.perspectiveM(mProjectMatrix, 0, 45, -ratio, 1, 100);
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
        a_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "a_MvpMatrix");
        u_TextureUnit = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glDetachShader(mProgram, vertexShader);
        GLES20.glDetachShader(mProgram, fragmentShader);
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        ShaderHelper.checkGLError("initProgram");
    }

    private void initTexture(Context context) {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, texture[0]);
        GLES20.glUniform1i(u_TextureUnit, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        try {
            for (int i = 0; i < mBitmaps.length; i++) {
                Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets().open(bitmapFilePath[i]));

                if (mBitmapBuffer == null) {
                    mBitmapBuffer = ByteBuffer.allocateDirect(mBitmap.getWidth() * mBitmap.getHeight() * 4);
                    mBitmapBuffer.order(ByteOrder.nativeOrder());
                }
                mBitmap.copyPixelsToBuffer(mBitmapBuffer);
                mBitmapBuffer.position(0);

                GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);
                mBitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

        ShaderHelper.checkGLError("initTexture");
    }

    private void initData(Context context) {
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

    @Override
    protected void onUpdate(float[] rotateMatrix) {
        Matrix.setIdentityM(mViewMatrix, 0);
        //设置 ViewMatrix
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //进行旋转
        Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, rotateMatrix, 0);

        //设置 MVPMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }


    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, texture[0]);
        GLES20.glUniformMatrix4fv(a_MvpMatrix, 1, false, mMVPMatrix, 0);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glVertexAttribPointer(a_Position, 4, GLES20.GL_FLOAT, false, 0, mVertexCoord);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(a_Position);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
        GLES20.glUseProgram(0);
        ShaderHelper.checkGLError("onDraw");
    }
}
