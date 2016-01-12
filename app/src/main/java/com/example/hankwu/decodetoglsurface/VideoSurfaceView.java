package com.example.hankwu.decodetoglsurface;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

@SuppressLint("ViewConstructor")
class VideoSurfaceView extends GLSurfaceView {

    VideoRender mRenderer;
    static public int number_of_play = 1;
    static public int row = 1;
    static public int col = 1;

    public VideoSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        mRenderer = new VideoRender(context);
        setRenderer(mRenderer);
    }

    @Override
    public void onResume() {
//        queueEvent(new Runnable(){
//            public void run() {
//
//            }});

        super.onResume();
    }

    private static class VideoRender
            implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
        private static String TAG = "VideoRender";

        private static final int FLOAT_SIZE_BYTES = 4;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
        private final float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -1.0f, -1.0f, 0, 0.f, 0.f,
                1.0f, -1.0f, 0, 1.f, 0.f,
                -1.0f,  1.0f, 0, 0.f, 1.f,
                1.0f,  1.0f, 0, 1.f, 1.f,
        };


        private FloatBuffer mTriangleVertices;

        private FloatBuffer tmpTriangleVertices;
        private FloatBuffer[] mTriangleVerticesCollector;

        private final String mVertexShader =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = uMVPMatrix * aPosition;\n" +
                        "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n";

        private final String mFragmentShader =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n";

        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];
        private int[] textures = new int[number_of_play];

        private int mProgram;
        private int mTextureID;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;

        SurfaceTexture[] mSurfaceTextures = new SurfaceTexture[number_of_play];

        private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

        //TODO: Think view port, maybe can use real resolution to set X,Y,Z position
        //TODO: Maybe it doesn't need to scale to min -1 -1 0 max 1 1 0.

        public VideoRender(Context context) {
            mTriangleVertices = ByteBuffer.allocateDirect(
                    mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTriangleVertices.put(mTriangleVerticesData).position(0);
            mTriangleVerticesCollector = new FloatBuffer[number_of_play];

            float delta_x = 2.0f / col;
            float delta_y = 2.0f / row;

            float global_x_start = -1;
            float global_y_start =  1;

            // " \ " direction is x++ y--

            for(int i=0;i<number_of_play;i++) {
                //TODO: initialize matrixs here;
                /* Default layout is using uniform partition
                        ||======================||
                        ||          |           ||
                        ||     1    |    2      ||
                        ||----------------------||
                        ||          |           ||
                        ||     3    |    4      ||
                        ||======================||
                 */


                int postition_x = i%col;
                int postition_y = i/col;

                // left bottom point
                float start_x = global_x_start + postition_x*delta_x;
                float start_y = global_y_start - postition_y*delta_y - delta_y;

               float[] tmpVerticesData = {
                        // X, Y, Z, U, V
                       start_x,         start_y,            0,  0.f, 0.f, //left bottom
                       start_x+delta_x, start_y,            0,   1.f, 0.f, //right bottom
                       start_x,         start_y+delta_y,    0,  0.f, 1.f, //left top
                       start_x+delta_x, start_y+delta_y, 0,   1.f, 1.f, //right top
                };

                mTriangleVerticesCollector[i] = ByteBuffer.allocateDirect(
                        mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();

                mTriangleVerticesCollector[i].put(tmpVerticesData).position(0);
            }


            Matrix.setIdentityM(mSTMatrix, 0);


        }

        @Override
        public void onDrawFrame(GL10 glUnused) {

            for(int i=0;i<number_of_play;i++) {
                mSurfaceTextures[i].updateTexImage();
                mSurfaceTextures[i].getTransformMatrix(mSTMatrix);
//                GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
//                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glUseProgram(mProgram);
                checkGlError("glUseProgram");

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[i]);

                tmpTriangleVertices = mTriangleVerticesCollector[i];

                tmpTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
                GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, tmpTriangleVertices);
                checkGlError("glVertexAttribPointer maPosition");
                GLES20.glEnableVertexAttribArray(maPositionHandle);
                checkGlError("glEnableVertexAttribArray maPositionHandle");

                tmpTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
                GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, tmpTriangleVertices);
                checkGlError("glVertexAttribPointer maTextureHandle");
                GLES20.glEnableVertexAttribArray(maTextureHandle);
                checkGlError("glEnableVertexAttribArray maTextureHandle");

                Matrix.setIdentityM(mMVPMatrix, 0);
                GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
                GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                checkGlError("glDrawArrays");
            }
            GLES20.glFinish();
        }

        @Override
        public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        }

        @Override
        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
            mProgram = createProgram(mVertexShader, mFragmentShader);
            if (mProgram == 0) {
                return;
            }
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkGlError("glGetAttribLocation aTextureCoord");
            if (maTextureHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aTextureCoord");
            }

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkGlError("glGetUniformLocation uMVPMatrix");
            if (muMVPMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uMVPMatrix");
            }

            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
            checkGlError("glGetUniformLocation uSTMatrix");
            if (muSTMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uSTMatrix");
            }


            GLES20.glGenTextures(number_of_play, textures, 0);

            for(int i=0;i<number_of_play;i++) {

                mTextureID = textures[i];
                GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
                checkGlError("glBindTexture mTextureID");

                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                        GLES20.GL_NEAREST);
                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                        GLES20.GL_LINEAR);

            /*
             * Create the SurfaceTexture and pass it to the MediaPlayer
             */
                mSurfaceTextures[i] = new SurfaceTexture(mTextureID);
            }

            // MediaPlayerController part
            MediaPlayerController.mediaPlayerControllerSingleton.setSurfaceTextures(mSurfaceTextures);
            try {
                String[] path = new String[number_of_play];
                for(int i=0;i<number_of_play;i++) {
                    path[i] = "/sdcard/Download/720_mute.mp4";
                }
                MediaPlayerController.mediaPlayerControllerSingleton.setDataSources(path);
                MediaPlayerController.mediaPlayerControllerSingleton.prepare();
                MediaPlayerController.mediaPlayerControllerSingleton.start();
            } catch (IOException e) {
                Log.d("HANK",e.toString());
                e.printStackTrace();
            }

        }

        synchronized public void onFrameAvailable(SurfaceTexture surface) {
            //updateSurface = true;
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            if (shader != 0) {
                GLES20.glShaderSource(shader, source);
                GLES20.glCompileShader(shader);
                int[] compiled = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] == 0) {
                    Log.e(TAG, "Could not compile shader " + shaderType + ":");
                    Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            int program = GLES20.glCreateProgram();
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e(TAG, "Could not link program: ");
                    Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        private void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

    }

}