package com.example.alex.testcft;

import android.app.Application;

import com.example.alex.testcft.Model.ImageProcessing;

import java.util.ArrayList;
import java.util.List;

public class AppCFT extends Application {

    //data
    private List<ImageProcessing> imageProcessings = new ArrayList<>();

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


}
