package com.jtl.opengl.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.jtl.opengl.helper.ShaderHelper;
import com.jtl.opengl.base.BaseRender;
import com.socks.library.KLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 15:14
 * 描述:
 * 更改:
 */
public class BitmapRender extends BaseRender {
    private static final String TAG = BitmapRender.class.getSimpleName();
    private Context mContext;
    //    private static final String VERTEX_SHADER_NAME = "shaders/point_circle.vert";
//    private static final String FRAGMENT_SHADER_NAME = "shaders/point_circle.frag";
    private static final String VERTEX_SHADER_NAME = "shaders/bitmap_vertex.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shaders/bitmap_frag.glsl";
    private static final String bitmapFile = "model/nba.jpg";
    private int mProgram;
    private int[] texture = new int[1];
    private int a_Position;
    private int a_TexCoord;
    private int u_TextureUnit;

    private Bitmap mBitmap;
    private FloatBuffer mTextureCoord;
    private FloatBuffer mVertexCoord;
    private ByteBuffer mBitmapBuffer;
    //纹理坐标
    private float[] textureCoord = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    //顶点坐标
    private float[] vertexCoord = new float[]{
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };

    @Override
    protected void createdGLThread(Context context) {
        mContext = context;

        initProgram(context);
        initData(context);
        initTexture();
    }

    private void initProgram(Context context) {
        int mVertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int mFragShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragShader);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        a_Position = GLES20.glGetAttribLocation(mProgram, "a_Position");
        a_TexCoord = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        u_TextureUnit = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glDetachShader(mProgram,mVertexShader);
        GLES20.glDetachShader(mProgram,mFragShader);
        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteShader(mFragShader);
        ShaderHelper.checkGLError("initProgram");
    }

    private void initData(Context context) {
        try {
            mBitmap = BitmapFactory.decodeStream(context.getAssets().open(bitmapFile));
            mBitmapBuffer = ByteBuffer.allocateDirect(mBitmap.getWidth() * mBitmap.getHeight() * 4);
            mBitmapBuffer.order(ByteOrder.nativeOrder());
            mBitmap.copyPixelsToBuffer(mBitmapBuffer);
            mBitmapBuffer.position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuffer coordByteBuffer = ByteBuffer.allocateDirect(textureCoord.length * 4);
        coordByteBuffer.order(ByteOrder.nativeOrder());
        mTextureCoord = coordByteBuffer.asFloatBuffer();
        mTextureCoord.put(textureCoord).position(0);

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexCoord = vertexByteBuffer.asFloatBuffer();
        mVertexCoord.put(vertexCoord).position(0);
    }

    private void initTexture() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//一个纹理单元的情况下默认激活TEXTURE_0.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glUniform1i(u_TextureUnit, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);
        ShaderHelper.checkGLError("initTexture");
    }

    @Override
    protected void onUpdate() {

    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_TexCoord);

        GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, true, 0, mVertexCoord);
        GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, true, 0, mTextureCoord);
//
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_TexCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        ShaderHelper.checkGLError("onDraw");
    }
}
