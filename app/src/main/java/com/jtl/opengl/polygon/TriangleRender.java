package com.jtl.opengl.polygon;

import android.content.Context;
import android.opengl.GLES20;

import com.jtl.opengl.base.BaseRender;
import com.jtl.opengl.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/30 10:38
 * 描述:三角形
 * 更改:
 */
public class TriangleRender extends BaseRender {
    private static final String TAG = TriangleRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/triangle_vertex.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/triangle_frag.glsl";
    private int mProgram;
    private int a_Position;
    private int u_Color;
    private FloatBuffer mVertexCoord;
    //顶点坐标
    private float[] vertexCoord = new float[]{
            0f, 0.75f,
            -1f, -0.25f,
            1f, -0.25f
    };

    private float[] color4f = new float[]{255/255f, 215/255f, 0, 1};
    @Override
    protected void createdGLThread(Context context) {
        initProgram(context);
        initData();

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
        u_Color = GLES20.glGetUniformLocation(mProgram, "u_Color");

        GLES20.glDetachShader(mProgram, vertexShader);
        GLES20.glDetachShader(mProgram, fragmentShader);
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        ShaderHelper.checkGLError("initProgram");
    }

    private void initData() {
        ByteBuffer positionBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
        positionBuffer.order(ByteOrder.nativeOrder());
        mVertexCoord = positionBuffer.asFloatBuffer();
        mVertexCoord.put(vertexCoord);
        mVertexCoord.position(0);
    }

    @Override
    protected void onUpdate() {

    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, false, 0, mVertexCoord);

        GLES20.glUniform4fv(u_Color, 1, color4f, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glDisableVertexAttribArray(a_Position);

        ShaderHelper.checkGLError("onDraw");
    }
}
