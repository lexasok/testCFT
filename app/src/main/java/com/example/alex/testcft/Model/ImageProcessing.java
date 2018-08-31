package com.example.alex.testcft.Model;

import android.view.MenuItem;

public class ImageProcessing {

    public static final int CODE_PROCESSING_ROTATE_90_CKW = 1;
    public static final int CODE_PROCESSING_ROTATE_90_CCW = 2;
    public static final int CODE_PROCESSING_ROTATE_180 = 3;
    public static final int CODE_PROCESSING_BLACK_AND_WITHE = 4;
    public static final int CODE_PROCESSING_MIRROW = 4;

    private int code;
    private MenuItem menuItem;

    public ImageProcessing(int code) {
        this.code = code;
    }

    public ImageProcessing(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
