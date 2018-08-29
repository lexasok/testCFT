package com.example.alex.testcft.ImageProcessing;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import com.example.alex.testcft.DataStructure.Process;
import com.example.alex.testcft.RVAdapter;

public class ProgressTask extends AsyncTask<Void, Integer, Void> {

    private ProgressBar progressBar;
    private RVAdapter rvAdapter;
    private Process process;

    public ProgressTask(ProgressBar progressBar, RVAdapter rvAdapter, Process process) {
        this.progressBar = progressBar;
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
    }

    @Override
    protected void onPostExecute(Void unused) {
        progressBar.setVisibility(View.GONE);
        rvAdapter.addProcess(process);
    }
}