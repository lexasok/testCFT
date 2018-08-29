package com.example.alex.testcft;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.example.alex.testcft.DataStructure.Process;

public class CustomProgressBar extends ProgressBar {

    private RVAdapter rvAdapter;
    private Process process;

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, RVAdapter rvAdapter, Process process) {
        super(context, attrs, defStyleAttr);

        this.rvAdapter = rvAdapter;
        this.process = process;
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);

        if (progress == this.getMax()) {
            rvAdapter.addProcess(process);
        }
    }
}
