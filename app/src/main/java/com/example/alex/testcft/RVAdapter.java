package com.example.alex.testcft;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends Adapter<RVAdapter.ProcessViewHolder> {

    //data
    private List<Bitmap> mData = new ArrayList<>();

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.raw_result_list_rv, viewGroup, false);

        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProcessViewHolder processViewHolder, int i) {

        Bitmap bitmap = mData.get(i);
        processViewHolder.imageView.setImageBitmap(bitmap);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addBitmap(Bitmap bitmap) {
        mData.add(0, bitmap);
        notifyDataSetChanged();
    }

    public class ProcessViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ProcessViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.resultImageViewRowRV);
        }
    }

}
