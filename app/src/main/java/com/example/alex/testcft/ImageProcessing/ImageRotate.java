package com.example.alex.testcft.ImageProcessing;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageRotate {

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return result;
    }
}
