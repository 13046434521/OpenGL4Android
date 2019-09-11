package com.jtl.opengl.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.jtl.opengl.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * YUV-NV12图像数据渲染
 */
public class BackGroundNV12RenderNew {
    private static final String TAG = BackGroundNV12RenderNew.class.getSimpleName();
    private static final float[] coordVertices = new float[]{
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };
    private static float[] squareVertices = new float[]{
            -1.0f, -1.0f,
            -1.0f, +1.0f,
            +1.0f, -1.0f,
            +1.0f, +1.0f,
    };
    private int programHandle = -1;
    private int positionHandle = -1;
    private int coordHandle = -1;
    private int ySampler = -1;
    private int uvSampler = -1;
    private int yTextureId = -1;
    private int uvTextureId = -1;
    private ByteBuffer verticeBuffer;
    private ByteBuffer coordBuffer;

    public BackGroundNV12RenderNew() {
        createBuffers(squareVertices, coordVertices);
    }

    private void createBuffers(float[] vert, float[] coord) {
        verticeBuffer = ByteBuffer.allocateDirect(vert.length * 4);
        verticeBuffer.order(ByteOrder.nativeOrder());
        verticeBuffer.asFloatBuffer().put(vert);
        verticeBuffer.position(0);

        coordBuffer = ByteBuffer.allocateDirect(coord.length * 4);
        coordBuffer.order(ByteOrder.nativeOrder());
        coordBuffer.asFloatBuffer().put(coord);
        coordBuffer.position(0);
    }

    public void createOnGLThread(Context context) {
        int vertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, "shader/yuv_vert.glsl");
        int fragmentShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, "shader/yuv_frag.glsl");

        programHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(programHandle, vertexShader);
        GLES20.glAttachShader(programHandle, fragmentShader);
        GLES20.glLinkProgram(programHandle);
        GLES20.glUseProgram(programHandle);
        positionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition");
        if (positionHandle == -1) {
            throw new RuntimeException("Could not get attribute location for vPosition");
        }

        coordHandle = GLES20.glGetAttribLocation(programHandle, "a_texCoord");
        if (coordHandle == -1) {
            throw new RuntimeException("Could not get attribute location for a_texCoord");
        }

        ySampler = GLES20.glGetUniformLocation(programHandle, "tex_y");
        if (ySampler == -1) {
            throw new RuntimeException("Could not get uniform location for tex_y");
        }

        uvSampler = GLES20.glGetUniformLocation(programHandle, "tex_u");
        if (uvSampler == -1) {
            throw new RuntimeException("Could not get uniform location for tex_u");
        }

        int[] ytextures = new int[2];
        GLES20.glGenTextures(2, ytextures, 0);
        yTextureId = ytextures[0];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glUniform1i(ySampler, 0);
        uvTextureId = ytextures[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glUniform1i(uvSampler, 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void drawFrame(ByteBuffer yData, ByteBuffer uvData, int width, int height) {

        GLES20.glUseProgram(programHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yData);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, width / 2, height / 2, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, uvData);


        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticeBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(coordHandle, 2, GLES20.GL_FLOAT, false, 0, coordBuffer);
        GLES20.glEnableVertexAttribArray(coordHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(coordHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }


    //横屏映射
//    private static final float[] coordVertices = new float[]{
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//            0.0f, 1.0f,
//            1.0f, 1.0f
//    };
//    //正常映射
//    private static final float[] SCREEN_TEXCOORDS = new float[]{
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 1.0f,
//            1.0f, 0.0f,
//    };

    // 我的
//    private static float[] squareVertices =  new float[]{
//            -1.0f, -1.0f, 0.0f, -1.0f, +1.0f, 0.0f, +1.0f, -1.0f, 0.0f, +1.0f, +1.0f, 0.0f,
//    };
//    private static float[] coordVertices = new float[]{
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 1.0f,
//            1.0f, 0.0f,
//    };

}

