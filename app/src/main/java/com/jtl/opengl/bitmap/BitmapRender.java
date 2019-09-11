package com.jtl.opengl.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
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
 * 日期:Created in 2019/8/27 15:14
 * 描述:
 * 更改:
 */
public class BitmapRender extends BaseRender {
    private static final String TAG = BitmapRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/bitmap_vert.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/bitmap_frag.glsl";
    private static final String bitmapFile = "drawable/nba.jpg";
    private int mProgram;
    private int[] texture = new int[1];
    private int a_Position;
    private int a_TexCoord;
    private int u_TextureUnit;
    private int u_MvpMatrix;

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

    //正交矩阵
    private float[] orthoMatrix = new float[16];

    @Override
    protected void createdGLThread(Context context) {
        initProgram(context);
        initData(context);
        initTexture();
    }


    @Override
    protected void onSurfaceChanged(float width, float height) {
        float ratio = width > height ? width / height : height / width;

        if (width > height) {
            //landscape 横屏 width:2340 > height:856  orthoMatrix:[0.36581194, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, -0.0, -0.0, -0.0, 1.0]
            // 把每个点的x值缩小了
            Matrix.orthoM(orthoMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
        } else {
            //portrait 竖屏 height:2092 > width:1080   orthoMatrix:[1.0, 0.0, 0.0, 0.0, 0.0, 0.5162524, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, -0.0, -0.0, -0.0, 1.0]
            // 把每个点的y值缩小了
            Matrix.orthoM(orthoMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
        }

        KLog.w(TAG, "height:" + height + " width:" + width + " " + Arrays.toString(orthoMatrix));
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
        u_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "u_MvpMatrix");

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
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);在过滤缩小时，选择带有mipmap的选项会用到
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBitmapBuffer);
        ShaderHelper.checkGLError("initTexture");
    }

    @Override
    protected void onUpdate(float[] data) {

    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glUniformMatrix4fv(u_MvpMatrix, 1, false, orthoMatrix, 0);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_TexCoord);

        //public static void glVertexAttribPointer(插槽位置,有几个分量（x,y,z,w）,数据类型,是否归一化,0,数据)
        //告诉GPU如何遍历VBO的内存块
        GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, true, 0, mVertexCoord);
        GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, true, 0, mTextureCoord);

        //绘制图元类型，从第几个点开始绘制，绘制多少个点
        //他会遍历，vbo里的数据。并把点分别传入4个shader里，他们的viewMatrix，projectMatrix，是一模一样的
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_TexCoord);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        ShaderHelper.checkGLError("onDraw");
    }
}
