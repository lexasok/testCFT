package com.example.alex.testcft;

import android.app.Application;
import android.graphics.Bitmap;

import com.example.alex.testcft.Model.ImageProcessing;

import java.util.ArrayList;
import java.util.List;

public class AppCFT extends Application {

    //data
    private List<ImageProcessing> imageProcesses = new ArrayList<>();
    private Bitmap imageMain;
    private List<Bitmap> results = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public List<ImageProcessing> getImageProcesses() {
        return imageProcesses;
    }

    public void addImageProcessing(ImageProcessing imageProcessing) {
        imageProcesses.add(imageProcessing);
    }

    public void deleteImageProcessing(ImageProcessing imageProcessing) {
        imageProcesses.remove(imageProcessing);
    }

    public void setImageMain(Bitmap bitmap) {
        imageMain = bitmap;
    }

    public Bitmap getImageMain() {
        return imageMain;
    }

    public List<Bitmap> getResults() {
        return results;
    }

    public void addBitmapToResults(Bitmap bitmap) {
        results.add(bitmap);
    }

    public void deleteBitmapFromResults(Bitmap bitmap) {
        results.remove(bitmap);
    }
}
