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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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
    DualBitmap dual;
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
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bd = new BitmapDrawable(bitmap);
        view.setBackground(bd);
        bd.setGravity(Gravity.CENTER);
        dual = new DualBitmap(bitmap);
        view.setOnTouchListener(this);
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
    boolean state;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long timeStart, timeEnd;
        if (state) {
            timeStart = System.currentTimeMillis();
            for (int i=0; i<100;i++) {
                dual.fillC();
            }
            timeEnd = System.currentTimeMillis();
            Log.i("Profiling", "C:"+ (timeEnd - timeStart));
        } else {
            timeStart = System.currentTimeMillis();
            for (int i=0; i<100;i++) {
                dual.fill();
            }
            timeEnd = System.currentTimeMillis();
            Log.i("Profiling", "Java:"+(timeEnd - timeStart));
        }

        state = !state;
        view.invalidate();
        return false;
    }
}
