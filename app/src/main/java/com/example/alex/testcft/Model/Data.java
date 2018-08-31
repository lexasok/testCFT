package com.example.alex.testcft.Model;

import android.graphics.Bitmap;

import java.util.List;

public class Data {

    private Bitmap mImageMain;
    private List<Bitmap> mData;
    private List<Bitmap> mProcesses;

    public Data(Bitmap imageMain, List<Bitmap> data, List<Bitmap> processes) {
        mImageMain = imageMain;
        mData = data;
        mProcesses = processes;
    }

    public Bitmap getmImageMain() {
        return mImageMain;
    }

    public List<Bitmap> getmData() {
        return mData;
    }

    public List<Bitmap> getmProcesses() {
        return mProcesses;
    }
}
