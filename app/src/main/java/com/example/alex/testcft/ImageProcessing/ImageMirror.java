package com.example.alex.testcft.ImageProcessing;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

public class ImageMirror {

    public static Bitmap make(Bitmap bitmap)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = bitmap;
        Bitmap result = null;

        try {
            result = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
            result.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return result;
    }
}
