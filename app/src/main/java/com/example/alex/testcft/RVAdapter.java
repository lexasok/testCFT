package com.example.alex.testcft;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.ViewGroup;

public class RVAdapter extends Adapter<RVAdapter.ProcessViewHolder> {

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessViewHolder processViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProcessViewHolder extends RecyclerView.ViewHolder {

        public ProcessViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
