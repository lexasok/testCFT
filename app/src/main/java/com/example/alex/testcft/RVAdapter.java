package com.example.alex.testcft;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alex.testcft.ImageProcessing.ProgressTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RVAdapter extends Adapter<RVAdapter.ProcessViewHolder> {

    //data
    private List<Bitmap> mData = new ArrayList<>();
    private Context mContext;
    private ImageView imageMain;

    RVAdapter(Context context, ImageView imageMain) {
        mContext = context;
        this.imageMain = imageMain;

    }

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.raw_result_list_rv, viewGroup, false);

        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProcessViewHolder processViewHolder, final int i) {

        Bitmap bitmap = mData.get(i);
        processViewHolder.imageView.setImageBitmap(bitmap);
        processViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addBitmap(Bitmap bitmap) {
        mData.add(0, bitmap);
        notifyDataSetChanged();
    }

    public void addBitmapsList(List<Bitmap> bitmaps) {
        //clearing for history
        mData.clear();
        mData.addAll(bitmaps);
        //sorting
        Collections.reverse(mData);
        notifyDataSetChanged();
    }

    class ProcessViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        ProcessViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.resultImageViewRowRV);
        }
    }

    private void showPopup(final View view, final int pos) {
        final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(mContext, view);
        final MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_rv_item_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        mData.remove(pos);
                        notifyDataSetChanged();
                        break;
                    case R.id.setAsMainImage:
                        imageMain.setImageBitmap(mData.get(pos));
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public List<Bitmap> getData() {
        return mData;
    }

    //    @Override
//    public void onViewDetachedFromWindow(@NonNull ProcessViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.imageView.setOnClickListener(null);
//    }
}
