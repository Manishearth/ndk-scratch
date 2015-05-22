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
        Log.i("h", height + "");
        Log.i("w", width + "");
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bd = new BitmapDrawable(bitmap);
        view.setBackground(bd);

        for (int i = 1; i < height; i++) {
            for (int j=1; j < width; j++) {
                bitmap.setPixel(j, i , Color.RED);
            }
        }
        view.setOnTouchListener(this);
        setContentView(view);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

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
        Log.i("foo", "x:"+ event.getX() + " y:" +event.getY());
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        for (int i = (int) event.getY(); i < height && i < event.getY() + 20; i++) {
            for (int j = (int) event.getX(); j < width && j < event.getX() + 20; j++) {
                bitmap.setPixel(j, i , color);
            }
        }
        //view.setBackground(bd);
        //setContentView(view);
        view.invalidate();
        return false;
    }
}
