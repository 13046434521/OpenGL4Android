package com.jtl.opengl.polygon;

import android.content.Context;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jtl.opengl.Constant;
import com.jtl.opengl.base.BaseGLSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.jtl.opengl.Constant.TRIANGLE;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/28 18:08
 * 描述:
 * 更改:
 */
public class PolygonGLSurface extends BaseGLSurface {
    private static final String TAG = PolygonGLSurface.class.getSimpleName();
    private TriangleRender mTriangleRender;
    private CubeRender mCubeRender;
    private volatile float[] mRotateMatrix = new float[16];

    private float mAngelRatio = 45f;
    private float mEventX = 0;
    private float mEventY = 0;
    private float mWidth = 0;
    private float mHeight = 0;

    private @Constant.Polygon
    int mType = TRIANGLE;

    public PolygonGLSurface(Context context) {
        super(context);
    }

    public PolygonGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mTriangleRender = new TriangleRender();
        mTriangleRender.createdGLThread(getContext().getApplicationContext());

        mCubeRender = new CubeRender();
        mCubeRender.createdGLThread(getContext().getApplicationContext());

        Matrix.setIdentityM(mRotateMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        mWidth = width;
        mHeight = height;
        mCubeRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mType == TRIANGLE) {
            mTriangleRender.onDraw();
        } else if (mType == Constant.CUBE) {
            mCubeRender.onUpdate(mRotateMatrix);
            mCubeRender.onDraw();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mType == Constant.CUBE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mEventX = event.getX();
                    mEventY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Matrix.setIdentityM(mRotateMatrix, 0);
                    Matrix.rotateM(mRotateMatrix, 0, mEventX - event.getX() / mWidth * mAngelRatio, 1, 0, 0);
                    Matrix.rotateM(mRotateMatrix, 0, mEventY - event.getY() / mHeight * mAngelRatio, 0, 1, 0);

                    mEventX = event.getX();
                    mEventY = event.getY();

                    setRotateMatrix(mRotateMatrix);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mEventX = 0;
                    mEventY = 0;
                    break;
            }
        }
        return true;
    }

    public void setRotateMatrix(float[] rotateMatrix) {
        this.mRotateMatrix = rotateMatrix;
    }

    public void setType(int type) {
        mType = type;
    }
}
