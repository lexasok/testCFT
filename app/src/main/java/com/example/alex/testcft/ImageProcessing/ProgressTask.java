package com.example.alex.testcft.ImageProcessing;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alex.testcft.DataStructure.Process;
import com.example.alex.testcft.R;
import com.example.alex.testcft.RVAdapter;

public class ProgressTask extends AsyncTask<Void, Integer, Void> {

    private ProgressBar progressBar;
    private RVAdapter rvAdapter;
    private Process process;
    private TextView percentIndicator;
    private RelativeLayout progressContainer;

    public ProgressTask(RelativeLayout progressContainer, RVAdapter rvAdapter, Process process) {
        this.progressContainer = progressContainer;
        this.progressBar = progressContainer.findViewById(R.id.progressBar);
        this.percentIndicator = progressContainer.findViewById(R.id.percentIndicator);
        this.rvAdapter = rvAdapter;
        this.process = process;
    }

    @Override
    protected Void doInBackground(Void... unused) {
        for (int i = 0; i < 100; i++) {
            publishProgress(i);
            SystemClock.sleep(50);
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
        rvAdapter.addProcess(process);
    }
}