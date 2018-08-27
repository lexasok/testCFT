package com.example.alex.testcft;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alex.testcft.DataStructure.Process;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends Adapter<RVAdapter.ProcessViewHolder> {

    private List<Process> mData = new ArrayList<>();

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.raw_result_list_rv, viewGroup, false);

        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessViewHolder processViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addProcess(Process process) {
        mData.add(process);
    }

    public class ProcessViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView imageView;

        public ProcessViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBarRowRV);
            imageView = itemView.findViewById(R.id.resultImageViewRowRV);
        }
    }
}
