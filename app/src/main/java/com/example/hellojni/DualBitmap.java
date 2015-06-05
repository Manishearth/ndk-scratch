package com.example.hellojni;

import android.graphics.Bitmap;

/**
 * Wrapper around a bitmap which can be accessed from
 * both Java and C.
 */
public class DualBitmap {


    Bitmap bitmap;
    long cBitmap;


    native long LOCK(Bitmap bitmap, int w, int h);
    native long UNLOCK(Bitmap bitmap);
    native long drawC(long CHandle);
    public static native int randomColor();
    public static native void copyBuf(long cbin, long cbout);
    public static native void freeWrap(long cHandle);

    /**
     * Creates a bitmap and locks it
     * @param width Width of the bitmap
     * @param height Height of the bitmap
     */
    public DualBitmap(int width, int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cBitmap = LOCK(bitmap, width, height);
    }

    /**
     * Creates a com.example.hellojni.DualBitmap from an existing Java Bitmap
     * @param bmp the bitmap to use
     */
    public DualBitmap(Bitmap bmp) {
        bitmap = bmp;
        cBitmap = LOCK(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * Closes the bitmap. This will make
     * C methods crash, so do not use this object
     * after closing.
     */
    public void close() {
        // TODO: free() the inner CBitmapHandle
        UNLOCK(bitmap);
        freeWrap(cBitmap);
    }

    /**
     *
     * @return Inner Java Bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Returns a pointer to the C bitmap representation
     * This is a void* which can be casted to a CBitmapHandle*
     * @return a pointer to the C bitmap representation
     */
    public long getCBitmap() {
        return cBitmap;
    }

    /**
     * Fills the bitmap with a random color, from Java
     */
    public void fill() {
        int color = randomColor();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int i=0; i< w; i++) {
            for (int j=0; j<h; j++) {
                bitmap.setPixel(i, j, color);
            }
        }
    }
    /**
     * Fills the bitmap with a random color, from C++
     */
    public void fillC() {
        drawC(cBitmap);
    }

    /**
     * memcpy buffer of DualBitmap to other DualBitmap
     * @param out DualBitmap that should receive the buffer contents
     */
    public void copyTo(DualBitmap out) {
        copyBuf(cBitmap, out.cBitmap);
    }


}
