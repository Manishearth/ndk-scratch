/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;

import java.util.Random;


public class HelloJni extends Activity implements View.OnTouchListener
{
    Bitmap bitmap;
    BitmapDrawable bd;
    SurfaceView view;
    int height, width;
    long cppLayer;
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        /*
        TextView  tv = new TextView(this);
        tv.setText( stringFromJNI() );*/

        view = new SurfaceView(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
        Log.i("Manish h", height + " Manish");
        Log.i("Manish w", width + " Manish");
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bd = new BitmapDrawable(bitmap);
        view.setBackground(bd);

        long tStart = System.currentTimeMillis();
        draw1(bitmap, width, height);
        long tEnd = System.currentTimeMillis();
        Log.i("t1:", (tEnd - tStart) + " Manish");
        tStart = System.currentTimeMillis();
        draw2(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("t2:", (tEnd - tStart) + " Manish");

        view.setOnTouchListener(this);
        setContentView(view);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    public native void  draw1(Bitmap bitmap, int w, int h);
    public native void  draw2(Bitmap bitmap, int w, int h);
    public native void  draw3(Bitmap bitmap, int w, int h);
    public native void  draw4(Bitmap bitmap, int w, int h);
    public native void  draw5(Bitmap bitmap, int w, int h);
    public native void  draw6(Bitmap bitmap, int w, int h);
    public native void  LOCK(Bitmap bitmap, int w, int h);

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long tStart, tEnd;
        /*
        tStart = System.currentTimeMillis();
        draw1(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(LockPaint 1)    "+(tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw2(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintLockCopy 1)"+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw3(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintCopy 1)    "+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw4(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintOnly 1)    "+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw5(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(LockOnly 1)     "+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw6(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintBuf 1)     "+ (tEnd - tStart));


        */
        LOCK(bitmap, width, height);
        /*
        tStart = System.currentTimeMillis();
        draw1(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(Lock 2)"+(tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw2(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(Copy 2)"+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw3(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintCopy 2)"+ (tEnd - tStart));

        tStart = System.currentTimeMillis();
        draw1(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(Lock 3)"+(tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw2(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(Copy 3)"+ (tEnd - tStart));
        tStart = System.currentTimeMillis();
        draw3(bitmap, width, height);
        tEnd = System.currentTimeMillis();
        Log.i("Memory", "(PaintCopy 3)"+ (tEnd - tStart));
*/
        view.invalidate();
        return false;
    }
}
