package com.example.alex.testcft.HistoryManagment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.alex.testcft.AppCFT;
import com.example.alex.testcft.MainActivity;
import com.example.alex.testcft.R;
import com.example.alex.testcft.RVAdapter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HistoryLoader {

    private static final String APP_PREFERENCES = "images_preferences";
    private static final String KEY_IMAGES_URI_APP_PREFERENCES = "images_URI";

    private Context mContext;
    private RVAdapter rvAdapter;
    private SharedPreferences preferences;

    public HistoryLoader(Context appContext, RVAdapter rvAdapter) {
        this.mContext = appContext;
        this.rvAdapter = rvAdapter;
        this.preferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    public void load() {
        //getting paths
        Set<String> imagesURI = preferences.getStringSet(
                KEY_IMAGES_URI_APP_PREFERENCES, new LinkedHashSet<String>());
        //sorting
        //getting bitmaps
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String str : imagesURI) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(str);
                bitmaps.add(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmaps.size() > 0) {
            //show bitmaps
            rvAdapter.addBitmapsList(bitmaps);
            showHistoryToast();
        } else {
            showHaveNoCashesToast();
        }
    }

    private void showHistoryToast() {
        Toast.makeText(
                mContext, R.string.toast_history, Toast.LENGTH_SHORT).show();
    }

    private void showHaveNoCashesToast() {
        Toast.makeText(
                mContext, R.string.toast_have_no_cashes, Toast.LENGTH_SHORT).show();
    }
}
