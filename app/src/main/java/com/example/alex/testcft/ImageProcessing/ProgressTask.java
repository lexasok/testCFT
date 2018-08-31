package com.example.alex.testcft.ImageProcessing;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alex.testcft.R;
import com.example.alex.testcft.RVAdapter;

import java.util.Random;

public class ProgressTask extends AsyncTask<Void, Integer, Void> {

    private static final int MIN_DELAY = 5000;
    private static final int MAX_DELAY = 30000;
    private static final int MIN_PERCENT = 0;
    private static final int MAX_PERCENT = 100;

    private ProgressBar progressBar;
    private RVAdapter rvAdapter;
    private Bitmap result;
    private TextView percentIndicator;
    private RelativeLayout progressContainer;
    private View button;
    private int delay;
    private HistorySaver historySaver;

    public ProgressTask(RelativeLayout progressContainer, RVAdapter rvAdapter, Bitmap result, HistorySaver historySaver) {
        this.progressContainer = progressContainer;
        this.progressBar = progressContainer.findViewById(R.id.progressBar);
        this.percentIndicator = progressContainer.findViewById(R.id.percentIndicator);
        this.rvAdapter = rvAdapter;
        this.result = result;
        this.button = null;
        Random random = new Random();
        delay = MIN_DELAY + random.nextInt(MAX_DELAY + 1 - MIN_DELAY);
        this.historySaver = historySaver;
    }

    @Override
    protected Void doInBackground(Void... unused) {
        for (int i = MIN_PERCENT; i < MAX_PERCENT; i++) {
            publishProgress(i);
            SystemClock.sleep(delay/MAX_PERCENT);
        }
        return (null);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]+1);
        String percents = values[0]+1 + " %";
        percentIndicator.setText(percents);
    }

    @Override
    protected void onPostExecute(Void unused) {
        progressContainer.setVisibility(View.GONE);
        historySaver.save(result);
        rvAdapter.addBitmap(result);
    }
}