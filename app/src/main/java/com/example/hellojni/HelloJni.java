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
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.os.Bundle;

import java.util.Random;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class HelloJni extends Activity implements OnTouchListener
{
    Bitmap b1;
    Bitmap b2;
    SurfaceView view;
    int height, width;
    DualBitmap dual1;
    DualBitmap dual2;

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
        height = height / 2;
        width = width / 2;
        dual1 = new DualBitmap(width, height);
        dual2 = new DualBitmap(width, height);
        dual1.fillC();
        dual2.fillC();
        b1 = dual1.getBitmap();
        b2 = dual2.getBitmap();
        view.setOnTouchListener(this);
        current = 1;
        //dual1.fillC();
        //dual2.fillC();
        setContentView(view);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();



    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }
    int current;


    @SuppressLint("NewApi")
    void fillSwap(Surface surface) {

        Canvas can = surface.lockCanvas(new Rect(0,0,0,0));
        if (current == 1) {
            dual2.fillC();
            can.drawBitmap(b2, 0, 0, null);
        } else {
            dual1.fillC();
            can.drawBitmap(b1, 0, 0, null);
        }

        surface.unlockCanvasAndPost(can);
        current = 3 - current;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Surface s = view.getHolder().getSurface();

        long timeStart, timeEnd;
        current = 1;

        //noinspection InfiniteLoopStatement

            timeStart = System.currentTimeMillis();
            for (int i=0; i<1000;i++) {
                fillSwap(s);
            }
            timeEnd = System.currentTimeMillis();
            Log.i("Profiling", "SwapFillSurf:"+ (timeEnd - timeStart));

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        return false;
    }
}
