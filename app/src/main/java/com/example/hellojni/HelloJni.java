/*
 * Originally from Romain Guy (romainguy@urious-creature.com)'s source code.
 */

package com.example.hellojni;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL11;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;

public class HelloJni extends Activity {
    private TextureView mTextureView;
    private FloatBuffer mVertices;
    private FloatBuffer mTexes;
    private final float[] mVerticesData = {
            -0.5f,  0.5f, // Top-left
            0.5f,  0.5f, // Top-right
            0.5f, -0.5f, // Bottom-right
            -0.5f,  0.5f, // Top-left
            0.5f, -0.5f, // Bottom-right
            -0.5f, -0.5f // Bottom-left
    };
    private final float[] mTexData = {
            0.0f, 0.0f, // Top-left
            1.0f, 0.0f, // Top-right
            1.0f, 1.0f, // Bottom-right
            0.0f, 0.0f, // Top-left
            1.0f, 1.0f, // Bottom-right
            0.0f, 1.0f  // Bottom-left
    };
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mTexes = ByteBuffer.allocateDirect(mTexData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexes.put(mTexData).position(0);

        mTextureView = new TextureView(this);
        mTextureView
                .setSurfaceTextureListener(new GLSurfaceTextureListener());
        setContentView(mTextureView);
    }

    private class RenderThread extends Thread {
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private static final String TAG = "RenderThread";
        private SurfaceTexture mSurface;
        private EGLDisplay mEglDisplay;
        private EGLSurface mEglSurface;
        private EGLContext mEglContext;
        private int mProgram;
        private EGL10 mEgl;
        private GL11 mGl;

        public RenderThread(SurfaceTexture surface) {
            mSurface = surface;
        }

        @Override
        public void run() {
            initGL();

            int attribPosition = GLES20.glGetAttribLocation(mProgram,
                    "position");
            checkGlError();

            GLES20.glEnableVertexAttribArray(attribPosition);
            checkGlError();

            int texHandle = GLES20.glGetAttribLocation(mProgram, "texCoord");
            GLES20.glEnableVertexAttribArray(texHandle);
            checkGlError();


            int[] textureHandle = new int[1];

            GLES20.glGenTextures(1, textureHandle, 0);
            // mSurface.attachToGLContext(textureHandle[0]);

            checkGlError();
            checkCurrent();


            while(true) {
                long timeStart = System.currentTimeMillis();
                for(int i=0; i<1000; i++) {
                    GLES20.glClearColor(1.0f, 1.0f, 0, 0);
                    checkGlError();

                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    checkGlError();
                    GLES20.glUseProgram(mProgram);
                    mVertices.position(0);
                    GLES20.glVertexAttribPointer(attribPosition, 2,
                            GLES20.GL_FLOAT, false, 0, mVertices);
                    checkGlError();
                    mTexes.position(0);
                    GLES20.glVertexAttribPointer(texHandle, 2,
                            GLES20.GL_FLOAT, false, 0, mTexes);
                    checkGlError();


                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
                    Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.RED);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
                    Log.d(TAG, "draw!!");
                    checkGlError();

                    if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
                        Log.e(TAG, "cannot swap buffers!");
                    }
                    checkEglError();
                }
                long timeEnd = System.currentTimeMillis();
                Log.e("Profiling:", ""+ (timeEnd - timeStart));

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {

                }
            }

        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void checkCurrent() {
            if (!mEglContext.equals(mEgl.eglGetCurrentContext())
                    || !mEglSurface.equals(mEgl
                    .eglGetCurrentSurface(EGL10.EGL_DRAW))) {
                checkEglError();
                if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface,
                        mEglSurface, mEglContext)) {
                    throw new RuntimeException(
                            "eglMakeCurrent failed "
                                    + GLUtils.getEGLErrorString(mEgl
                                    .eglGetError()));
                }
                checkEglError();
            }
        }

        private void checkEglError() {
            final int error = mEgl.eglGetError();
            if (error != EGL10.EGL_SUCCESS) {
                Log.e(TAG, "EGL error = 0x" + Integer.toHexString(error));
            }
        }

        private void checkGlError() {
            final int error = mGl.glGetError();
            if (error != GL11.GL_NO_ERROR) {
                Log.e(TAG, "GL error = 0x" + Integer.toHexString(error));
            }
        }

        private int buildProgram(String vertexSource, String fragmentSource) {
            final int vertexShader = buildShader(GLES20.GL_VERTEX_SHADER,
                    vertexSource);
            if (vertexShader == 0) {
                return 0;
            }

            final int fragmentShader = buildShader(
                    GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (fragmentShader == 0) {
                return 0;
            }

            final int program = GLES20.glCreateProgram();
            if (program == 0) {
                return 0;
            }

            GLES20.glAttachShader(program, vertexShader);
            checkGlError();

            GLES20.glAttachShader(program, fragmentShader);
            checkGlError();

            GLES20.glLinkProgram(program);
            checkGlError();

            int[] status = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status,
                    0);
            checkGlError();
            if (status[0] == 0) {
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                checkGlError();
            }

            return program;
        }

        private int buildShader(int type, String shaderSource) {
            final int shader = GLES20.glCreateShader(type);
            if (shader == 0) {
                return 0;
            }

            GLES20.glShaderSource(shader, shaderSource);
            checkGlError();
            GLES20.glCompileShader(shader);
            checkGlError();

            int[] status = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status,
                    0);
            if (status[0] == 0) {
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                return 0;
            }

            return shader;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void initGL() {
            final String vertexShaderSource = "attribute vec2 position;\n"
                    +
                    "attribute vec2 texCoord;\n"+
                    "varying vec2 FragTexCoord;\n"+
                    "void main () {\n" +
                    "   FragTexCoord = texCoord;\n"+
                    "   gl_Position = vec4(position, 0.0, 1.0);\n" +
                    "}";

            final String fragmentShaderSource = "precision mediump float;\n"
                    +
                    "uniform sampler2D tex;\n"+
                    "varying vec2 FragTexCoord;\n"+
                    "void main () {\n" +
                    "   gl_FragColor = texture2D(tex,FragTexCoord);\n" +
                    "}";

            mEgl = (EGL10) EGLContext.getEGL();

            mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed "
                        + GLUtils.getEGLErrorString(mEgl.eglGetError()));
            }

            int[] version = new int[2];
            if (!mEgl.eglInitialize(mEglDisplay, version)) {
                throw new RuntimeException("eglInitialize failed "
                        + GLUtils.getEGLErrorString(mEgl.eglGetError()));
            }

            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            int[] configSpec = {
                    EGL10.EGL_RENDERABLE_TYPE,
                    EGL_OPENGL_ES2_BIT,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 0,
                    EGL10.EGL_STENCIL_SIZE, 0,
                    EGL10.EGL_NONE
            };

            EGLConfig eglConfig = null;
            if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1,
                    configsCount)) {
                throw new IllegalArgumentException(
                        "eglChooseConfig failed "
                                + GLUtils.getEGLErrorString(mEgl
                                .eglGetError()));
            } else if (configsCount[0] > 0) {
                eglConfig = configs[0];
            }
            if (eglConfig == null) {
                throw new RuntimeException("eglConfig not initialized");
            }

            int[] attrib_list = {
                    EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE
            };
            mEglContext = mEgl.eglCreateContext(mEglDisplay,
                    eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            checkEglError();
            mEglSurface = mEgl.eglCreateWindowSurface(
                    mEglDisplay, eglConfig, mSurface, null);
            checkEglError();
            if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
                int error = mEgl.eglGetError();
                if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                    Log.e(TAG,
                            "eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW");
                    return;
                }
                throw new RuntimeException(
                        "eglCreateWindowSurface failed "
                                + GLUtils.getEGLErrorString(error));
            }

            if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface,
                    mEglSurface, mEglContext)) {
                throw new RuntimeException("eglMakeCurrent failed "
                        + GLUtils.getEGLErrorString(mEgl.eglGetError()));
            }
            checkEglError();

            mGl = (GL11) mEglContext.getGL();
            checkEglError();

            mProgram = buildProgram(vertexShaderSource,
                    fragmentShaderSource);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class GLSurfaceTextureListener implements
            SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            new RenderThread(surface).start();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }

    }
}
