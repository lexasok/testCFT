package com.example.alex.testcft.DataStructure;

import android.graphics.Bitmap;

public class Process {

    public static final int CODE_ROTATE_CKW = 0;
    public static final int CODE_ROTATE_CCW = 1;
    public static final int CODE_BLACK_AND_WHITE = 2;
    public static final int CODE_MIRROR_IMAGE = 3;

    private Bitmap image;
    private int processCode;

    public Bitmap getImage() {
        return image;
    }

    //getters & setters
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getProcessCode() {
        return processCode;
    }

    public void setProcessCode(int processCode) {
        this.processCode = processCode;
    }

    public Process(Bitmap image, int processCode) {
        this.image = image;
        this.processCode = processCode;

    }
}
