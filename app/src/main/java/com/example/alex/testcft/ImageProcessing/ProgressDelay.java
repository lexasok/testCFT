package com.example.alex.testcft.ImageProcessing;

import android.widget.ProgressBar;

public class ProgressDelay {

    private static final int DELAY = 10000;
    private static final int PROGRESS_LOADING_STEP = DELAY / 100;

    public static void progressDelay(ProgressBar progressBar) {
        for (int i = 0; i < DELAY; i += PROGRESS_LOADING_STEP ) {
            try {
                int progress = progressBar.getProgress();
                Thread.sleep(PROGRESS_LOADING_STEP);
                progress++;
                progressBar.setProgress(progress);
            } catch (Exception e) {

            }
        }
    }
}
