package com.example.alex.testcft.Model;

import android.graphics.Bitmap;
import android.view.MenuItem;

public class ImageProcessing {

    private Bitmap result;

    public ImageProcessing(Bitmap result) {
        this.result = result;
    }

    public Bitmap getResult() {
        return result;
    }
}
