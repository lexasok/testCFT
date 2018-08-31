package com.example.alex.testcft;

import android.app.Application;
import android.graphics.Bitmap;

import com.example.alex.testcft.Model.ImageProcessing;

import java.util.ArrayList;
import java.util.List;

public class AppCFT extends Application {

    //data
    private List<ImageProcessing> imageProcessings = new ArrayList<>();
    private Bitmap imageMain;
    private List<Bitmap> mData = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void addImageProcessing(ImageProcessing imageProcessing) {
        imageProcessings.add(imageProcessing);
    }

    public void deleteImageProcessing(ImageProcessing imageProcessing) {
        imageProcessings.remove(imageProcessing);
    }

    public void setImageMain(Bitmap bitmap) {
        imageMain = bitmap;
    }

    public Bitmap getImageMain() {
        return imageMain;
    }

    public List<Bitmap> getData() {
        return mData;
    }

    public void addBitmapToData(Bitmap bitmap) {
        mData.add(bitmap);
    }

    public void deleteBitmapFromData(Bitmap bitmap) {
        mData.remove(bitmap);
    }
}
