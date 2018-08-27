package com.example.alex.testcft.DataStructure;

public class Process {

    private static final int CODE_ROTATE_CKW = 0;
    private static final int CODE_ROTATE_CCW = 1;
    private static final int CODE_BLACK_AND_WHITE = 2;
    private static final int CODE_MIRROR_IMAGE = 3;

    private String image;
    private int processCode;

    public String getImage() {
        return image;
    }

    //getters & setters
    public void setImage(String image) {
        this.image = image;
    }

    public int getProcessCode() {
        return processCode;
    }

    public void setProcessCode(int processCode) {
        this.processCode = processCode;
    }

    public Process(String image, int processCode) {
        this.image = image;
        this.processCode = processCode;

    }
}
