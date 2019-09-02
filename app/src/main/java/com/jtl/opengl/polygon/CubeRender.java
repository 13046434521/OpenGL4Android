package com.jtl.opengl.polygon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.helper.ShaderHelper;
import com.socks.library.KLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
    private static final String bitmapFilePath = "model/zhangli.jpg";
    private Bitmap[] mBitmaps = new Bitmap[6];
    private short[] index = new short[]{
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };
    private int mProgram;
    private int[] texture = new int[1];
    private int mTexture;
    private int a_Position;
    private int a_TexCoord;
    private int a_MvpMatrix;
    private int u_TextureUnit;
    private Bitmap mBitmap;
    private ByteBuffer mBitmapBuffer;
    private FloatBuffer mTextureCoord;
    private FloatBuffer mVertexCoord;
    private ShortBuffer mIndexBuffer;
    private float[] textureCoord = new float[]{
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };
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

    private void initProgram(Context context) {
        mProgram = GLES20.glCreateProgram();
        int vertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int fragmentShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        a_Position = GLES20.glGetAttribLocation(mProgram, "a_Position");
        a_TexCoord = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        a_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "a_MvpMatrix");
        u_TextureUnit = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glDetachShader(mProgram, vertexShader);
        GLES20.glDetachShader(mProgram, fragmentShader);
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        KLog.i(TAG, "a_Position:" + a_Position + "  a_TexCoord:" + a_TexCoord + "  a_MvpMatrix:" + a_MvpMatrix + "  u_TextureUnit:" + u_TextureUnit + "  ");
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

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        ShaderHelper.checkGLError("initTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

        ShaderHelper.checkGLError("initTexture");
    }

    private void initData(Context context) {
        ByteBuffer textureBuffer = ByteBuffer.allocateDirect(textureCoord.length * 4);
        textureBuffer.order(ByteOrder.nativeOrder());
        mTextureCoord = textureBuffer.asFloatBuffer();
        mTextureCoord.put(textureCoord);
        mTextureCoord.position(0);

        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexCoord = vertexBuffer.asFloatBuffer();
        mVertexCoord.put(vertexCoord);
        mVertexCoord.position(0);

        ByteBuffer indexBuffer = ByteBuffer.allocateDirect(index.length * 2);
        indexBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer = indexBuffer.asShortBuffer();
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);

        try {
            for (int i = 0; i < 6; i++) {
                Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets().open(bitmapFilePath));
                mBitmaps[i] = mBitmap;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            mBitmap = BitmapFactory.decodeStream(context.getAssets().open(bitmapFilePath), new Rect(), options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBitmapBuffer = ByteBuffer.allocateDirect(mBitmap.getWidth() * mBitmap.getHeight() * 4);
        mBitmapBuffer.order(ByteOrder.nativeOrder());
        mBitmap.copyPixelsToBuffer(mBitmapBuffer);
        mBitmapBuffer.position(0);
    }

    @Override
    protected void onUpdate() {

    }

    void setViewPort(int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mTexture);
        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_TexCoord);

        GLES20.glUniformMatrix4fv(a_MvpMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glVertexAttribPointer(a_Position, 4, GLES20.GL_FLOAT, false, 0, mVertexCoord);
        GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, false, 2, mTextureCoord);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_TexCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

        ShaderHelper.checkGLError("onDraw");
    }
}
