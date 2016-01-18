package com.example.hankwu.decodetoglsurface.encode;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by HankWu_Office on 2016/1/18.
 */
public class SnapShot {
    public static SnapShot snapShot = new SnapShot();
    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,  1.0f, 0, 1.f, 1.f,
    };
    public FloatBuffer mTriangleVertices;
    private SurfaceTexture surfaceTexture = null;
    private Surface surface = null;
    private static final int FLOAT_SIZE_BYTES = 4;
    private EGLSurface eglSurface = null;
    private boolean bEnableSnapShot = false;


    public boolean isEnableSnapShot() {
        return bEnableSnapShot;
    }


    SnapShot() {
        mTriangleVertices= ByteBuffer.allocateDirect(
                mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    public void setSurface(Surface st) {
        surface = st;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setEglSurface(EGLSurface egls) {
        eglSurface = egls;
    }

    public EGLSurface getEglSurface() {
        return eglSurface;
    }
}
