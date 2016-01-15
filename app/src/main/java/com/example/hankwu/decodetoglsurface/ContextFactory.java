package com.example.hankwu.decodetoglsurface;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by charleswu-bc on 2015/11/26.
 */
public class ContextFactory implements GLSurfaceView.EGLContextFactory {
    private EGLContext context;
    private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
        int[] attr_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };

        context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attr_list);
        return context;
    }

    public void destroyContext(EGL10 egl, EGLDisplay display,EGLContext context)
    {}
    public EGLContext getContext(){
        return context;
    }
}