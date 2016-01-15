package com.example.hankwu.decodetoglsurface;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.example.hankwu.decodetoglsurface.encode.Encoder;
import com.example.hankwu.decodetoglsurface.encode.Recorder;

import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by charleswu-bc on 2015/11/26.
 */
public class WindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {
    public static final int Encode_Case = 1;
    public static final int Preview_Case = 2;
    private EGLSurface mEGLPreviewSurface;
    private EGLSurface mEGLEncodeSurface;
    private EGL10 egl10 = (EGL10) EGLContext.getEGL();

    private Surface mEncodeSurface = null;

    private final String TAG = this.getClass().getName();
    public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display,
                                          EGLConfig config, Object nativeWindow) {
        EGLSurface result = null;
        try {
            //if(GlobalInfo.isRecordMode() && !Recorder.getRecorder().isSetPreview())
            {
                mEGLPreviewSurface = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            }

            try {
                if (GlobalInfo.isRecordMode()) {
                    Recorder.getRecorder().Create();
                    mEncodeSurface = Recorder.getRecorder().getSurface();
                } else if (GlobalInfo.isMediaCodecMuxerMode()) {
                    Encoder.getEncoder().Create();
                    mEncodeSurface = Encoder.getEncoder().mEncodeSuface;
                }
                mEGLEncodeSurface = egl.eglCreateWindowSurface(display, config, mEncodeSurface, null);

            } catch(IOException e) {
                e.printStackTrace();
            }



        } catch (IllegalArgumentException e) {
            Log.e(TAG, "eglCreateWindowSurface (native)", e);
        }
        // this return will trigger Renderer
        result = mEGLPreviewSurface;
        return result;
    }

    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
        egl.eglDestroySurface(display, surface);
    }

    public void makeCurrent(EGLContext context, int c){
        if(c == Encode_Case) {
            if(mEGLEncodeSurface!=null) {
                egl10.eglMakeCurrent(egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY),
                        mEGLEncodeSurface, mEGLEncodeSurface, context);
            }
        }
        else if(c == Preview_Case){
            egl10.eglMakeCurrent(egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY),
                    mEGLPreviewSurface, mEGLPreviewSurface,context );
        }
    }

    public void swapBuffers(){
            egl10.eglSwapBuffers(egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY), mEGLEncodeSurface);
    }
}