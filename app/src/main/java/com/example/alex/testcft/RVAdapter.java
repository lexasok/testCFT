package com.example.alex.testcft;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alex.testcft.DataStructure.Process;
import com.example.alex.testcft.ImageProcessing.ImageRotater;
import com.example.alex.testcft.ImageProcessing.ProgressDelay;

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
        Process process = mData.get(i);
        Bitmap bitmap = process.getImage();



        switch (process.getProcessCode()) {
            case Process.CODE_ROTATE_CKW:
                bitmap = ImageRotater.RotateBitmap(process.getImage(), 90);;
                processViewHolder.imageView.setImageBitmap(bitmap);
                break;

            case Process.CODE_ROTATE_CCW:
                bitmap = ImageRotater.RotateBitmap(process.getImage(), -90);
                processViewHolder.imageView.setImageBitmap(bitmap);
                break;

            case Process.CODE_ROTATE_180:
                bitmap = ImageRotater.RotateBitmap(process.getImage(), 180);
                processViewHolder.imageView.setImageBitmap(bitmap);
                break;

            case Process.CODE_BLACK_AND_WHITE:

                break;

            case Process.CODE_MIRROR_IMAGE:

                break;
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addProcess(Process process) {
        mData.add(process);
        notifyDataSetChanged();
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
