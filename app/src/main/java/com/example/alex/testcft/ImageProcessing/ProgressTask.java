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

public class ProgressTask extends AsyncTask<Void, Integer, Void> {

    private ProgressBar progressBar;
    private RVAdapter rvAdapter;
    private Bitmap result;
    private TextView percentIndicator;
    private RelativeLayout progressContainer;
    private View button;

    public ProgressTask(RelativeLayout progressContainer, RVAdapter rvAdapter, Bitmap result) {
        this.progressContainer = progressContainer;
        this.progressBar = progressContainer.findViewById(R.id.progressBar);
        this.percentIndicator = progressContainer.findViewById(R.id.percentIndicator);
        this.rvAdapter = rvAdapter;
        this.result = result;
        this.button = null;
    }

    public ProgressTask(RelativeLayout progressContainer, RVAdapter rvAdapter, Bitmap result, View button) {
        this.progressContainer = progressContainer;
        this.progressBar = progressContainer.findViewById(R.id.progressBar);
        this.percentIndicator = progressContainer.findViewById(R.id.percentIndicator);
        this.rvAdapter = rvAdapter;
        this.result = result;
        this.button = button;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (button != null) {
            button.setClickable(false);
        }
    }

    @Override
    protected Void doInBackground(Void... unused) {
        for (int i = 0; i < 100; i++) {
            publishProgress(i);
            SystemClock.sleep(50);
        }
        if (button != null) {

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
        rvAdapter.addBitmap(result);
        if (button != null) {
            button.setClickable(true);
        }

    }
}