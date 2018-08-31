package com.example.alex.testcft.HistoryManagment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.format.Time;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class HistorySaver {

    private static final String APP_PREFERENCES = "images_preferences";
    private static final String KEY_IMAGES_URI_APP_PREFERENCES = "images_URI";

    private Context mContext;
    private SharedPreferences preferences;

    public HistorySaver(Context appContext) {
        mContext = appContext;
        preferences = mContext.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    public void save(Bitmap bitmap) {
        Set<String> imagesSet = preferences.getStringSet(
                KEY_IMAGES_URI_APP_PREFERENCES, new LinkedHashSet<String>());
        String path = saveFileToCash(bitmap);
        if (path != null) {
            imagesSet.add(path);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putStringSet(KEY_IMAGES_URI_APP_PREFERENCES, imagesSet).apply();
        }
    }

    private String saveFileToCash(Bitmap bitmap) {

        OutputStream outputStream = null;

        try {
            File file = new File(mContext.getCacheDir(), generateFileName());

            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateFileName() {

        Time time = new Time();
        time.setToNow();

        StringBuilder name = new StringBuilder();
        name.append(time.year);
        name.append(time.month);
        name.append(time.monthDay);
        name.append(time.hour);
        name.append(time.minute);
        name.append(time.second);
        name.append(".jpg");

        return name.toString();
    }
}
