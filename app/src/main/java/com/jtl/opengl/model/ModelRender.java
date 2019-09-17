package com.jtl.opengl.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.bitmap.BitmapRender;
import com.jtl.opengl.helper.ShaderHelper;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.Arrays;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/16 13:32
 * 描述:
 * 更改:
 */
public class ModelRender extends BaseRender {
    private static final String TAG = BitmapRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/model_vert.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/model_frag.glsl";
    private int mProgram;
    private int[] texture = new int[1];
    private int a_Position;
    private int a_TexCoord;
    private int u_TextureUnit;
    private int u_MvpMatrix;

    //正交矩阵
    private float[] mvpMatrix = new float[16];
    private float[] rotateMatrix=new float[16];
    private ModelObj mModelObj;
    private volatile float scale=0.08f;
    private float width;
    private float height;
    @Override
    protected void createdGLThread(Context context) {
        initProgram(context);
    }

    public void initModelObj(ModelObj modelObj,Context context){
        mModelObj = modelObj;
        initData();
        initTexture(context);
        KLog.w(TAG,mModelObj.toString());
    }

    @Override
    protected void onSurfaceChanged(float width, float height) {
        float ratio = width > height ? width / height : height / width;
        this.width=width;
        this.height=height;
        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.translateM(mvpMatrix,0,0,0f,-0.1f);
//        Matrix.scaleM(mvpMatrix,0,0.4f,0.4f*width/height,0.4f);

        Matrix.scaleM(mvpMatrix,0,scale,scale*width/height,scale);
        KLog.w(TAG, "height:" + height + " width:" + width + " " + Arrays.toString(mvpMatrix));
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

        u_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "u_MvpMatrix");
        u_TextureUnit = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteShader(mFragShader);
        ShaderHelper.checkGLError("initProgram");
    }

    private void initData() {
        Matrix.setIdentityM(rotateMatrix,0);
        Matrix.setIdentityM(mvpMatrix,0);
    }

    private void initTexture(Context context) {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//一个纹理单元的情况下默认激活TEXTURE_0.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glUniform1i(u_TextureUnit, 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        try {
            Bitmap bitmap= BitmapFactory.decodeStream(context.getResources().getAssets().open("model/"+mModelObj.mModelMtl.getMap_Kd_Data()));
            KLog.w(TAG,mModelObj.mModelMtl.getMap_Kd_Data());
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);在过滤缩小时，选择带有mipmap的选项会用到
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        ShaderHelper.checkGLError("initTexture");
    }

    @Override
    protected void onUpdate(float[] data) {

    }


    @Override
    protected void onDraw() {
        Matrix.setIdentityM(rotateMatrix,0);
        Matrix.rotateM(rotateMatrix,0,0.3f,0,1,0);
        Matrix.multiplyMM(mvpMatrix,0,mvpMatrix,0,rotateMatrix,0);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUseProgram(mProgram);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glUniform1i(u_TextureUnit, 0);
        GLES20.glUniformMatrix4fv(u_MvpMatrix, 1, false, mvpMatrix, 0);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_TexCoord);

        //public static void glVertexAttribPointer(插槽位置,有几个分量（x,y,z,w）,数据类型,是否归一化,0,数据)
        //告诉GPU如何遍历VBO的内存块
        GLES20.glVertexAttribPointer(a_Position, 3, GLES20.GL_FLOAT, false, 0, mModelObj.getVertexBuffer());
        GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, false, 0, mModelObj.getTextureBuffer());

        //绘制图元类型，从第几个点开始绘制，绘制多少个点
        //他会遍历，vbo里的数据。并把点分别传入4个shader里，他们的viewMatrix，projectMatrix，是一模一样的
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mModelObj.getVertexCount());

        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_TexCoord);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        ShaderHelper.checkGLError("onDraw");
    }

    public void setScale(float scale) {
        this.scale = scale;

        Matrix.setIdentityM(mvpMatrix, 0);

        Matrix.translateM(mvpMatrix,0,0,0f,-0.1f);
        Matrix.scaleM(mvpMatrix,0,scale,scale*width/height,scale);
    }
}
