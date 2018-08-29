package com.example.alex.testcft;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.alex.testcft.DataStructure.Process;
import com.example.alex.testcft.ImageProcessing.ImageRotater;
import com.example.alex.testcft.ImageProcessing.ProgressDelay;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends Adapter<RVAdapter.ProcessViewHolder> {

    private List<Process> mData = new ArrayList<>();
    private Context mContext;

    public RVAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.raw_result_list_rv, viewGroup, false);

        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProcessViewHolder processViewHolder, int i) {
        Process process = mData.get(i);
        Bitmap bitmap;


        switch (process.getProcessCode()) {
            case Process.CODE_ROTATE_CKW:
                bitmap = ImageRotater.RotateBitmap(process.getImage(), 90);

                Glide
                        .with(mContext)
                        .load(bitmap)
                        .listener(new RequestListener<Drawable>() {

                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                ProgressDelay.progressDelay();

                                processViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }


                        })
                        .into(processViewHolder.imageView);
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
