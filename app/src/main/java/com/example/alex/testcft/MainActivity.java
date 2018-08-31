package com.example.alex.testcft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.alex.testcft.ImageProcessing.BlackAndWhiteImage;
import com.example.alex.testcft.ImageProcessing.ImageMirror;
import com.example.alex.testcft.ImageProcessing.ImageRotate;
import com.example.alex.testcft.ImageProcessing.ProgressTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 2;
    private static final String KEY_EXTRAS_GET_PHOTO = "data";
    private static final String APP_PREFERENCES = "images_preferences";
    private static final String KEY_IMAGES_URI_APP_PREFERENCES = "images_URI";

    //views
    private ConstraintLayout containerContentMain;
    private Button buttonChooseImage;
    private ImageView imageMain;
    private LinearLayout progressList;
    private EditText urlInput;
    private ProgressBar urlLoadingProgress;
    private LinearLayout dialogContainer;

    //adapters
    private RVAdapter rvAdapter;

    //preferences
    private SharedPreferences preferences;


    //Activity override methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPreferences();
        initViews();
        initRV();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                revertViewsByDialog();
                imageMain.setImageBitmap(
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

            showContent();
        }

        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get(KEY_EXTRAS_GET_PHOTO);
            revertViewsByDialog();
            imageMain.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {

            String query = urlInput.getText().toString();

            if (TextUtils.isEmpty(query)) {
                return false;
            } else {
                loadFromURL(query);
                return true;
            }
        } else super.onKeyUp(keyCode, event);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onTrimMemory(int level) {
        System.gc();
        super.onTrimMemory(level);
    }

    //fields initializing methods
    private void initViews() {
        containerContentMain = findViewById(R.id.containerContentMain);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        imageMain = findViewById(R.id.imageMain);
        //buttonRotate = findViewById(R.id.buttonRotate);
        progressList = findViewById(R.id.progressList);
    }

    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.resultListRV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new RVAdapter(this, imageMain);
        recyclerView.setAdapter(rvAdapter);
    }

    private void initPreferences() {
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    //show or hide views and menus
    public void showContent() {
        if (buttonChooseImage.getVisibility() == View.VISIBLE) {
            buttonChooseImage.setVisibility(View.GONE);
            containerContentMain.setVisibility(View.VISIBLE);
        }
    }

    public void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_rotate, popupMenu.getMenu());
        popupMenu.show();
    }

    public void showDialog(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.dialog_loading_image, (ViewGroup) findViewById(R.id.containerMain));
        urlInput = layout.findViewById(R.id.dialogLoadFromURLEditText);
        dialogContainer = layout.findViewById(R.id.loadImageDialogContainer);
        urlLoadingProgress = layout.findViewById(R.id.urlLoadingProgressBar);

        revertViewsByDialog();
    }

    private void revertViewsByDialog() {
        if (dialogContainer.getVisibility() == View.GONE) {
            dialogContainer.setVisibility(View.VISIBLE);
            containerContentMain.setVisibility(View.GONE);
            buttonChooseImage.setVisibility(View.GONE);
        } else {
            dialogContainer.setVisibility(View.GONE);
            containerContentMain.setVisibility(View.VISIBLE);
        }
    }

    //init buttons methods
    //dialog buttons
    public void openImageBrowser(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void openCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_FROM_CAMERA);
        }
    }

    public void loadFromURL(String query) {
        urlLoadingProgress.setVisibility(View.VISIBLE);
        Glide
                .with(this)
                .load(query)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model
                            , Target<Drawable> target, boolean isFirstResource) {
                        urlLoadingProgress.setVisibility(View.GONE);
                        showWrongUrlToast();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model
                            , Target<Drawable> target, DataSource dataSource
                            , boolean isFirstResource) {
                        urlLoadingProgress.setVisibility(View.GONE);
                        revertViewsByDialog();
                        return false;
                    }
                }).into(imageMain);
    }

    //processing buttons
    public void rotate(MenuItem item) {
        int id = item.getItemId();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageMain.getDrawable();
        Bitmap bitmapToProcess = bitmapDrawable.getBitmap();
        Bitmap result;
        switch (id) {
            case R.id.menu_item_rotate_90_ckw:
                result = ImageRotate.rotateBitmap(bitmapToProcess, 90);
                break;
            case R.id.menu_item_rotate_90_ccw:
                result = ImageRotate.rotateBitmap(bitmapToProcess, -90);
                break;
            case R.id.menu_item_rotate_180:
                result = ImageRotate.rotateBitmap(bitmapToProcess, 180);
                break;
            default: result = bitmapToProcess;
                break;
        }

        //loading
        if (result != null) {
            load(result);
        } else showOutOfMemoryToast();
    }

    public void doBlackAndWhite(View view) {
        //processing
        Bitmap result = BlackAndWhiteImage.make(
                ((BitmapDrawable)imageMain.getDrawable()).getBitmap()
        );

        //loading
        if (result != null) {
            load(result);
        } else  {
            showOutOfMemoryToast();
        }
    }

    public void mirrorImage(View view) {
        //processing
        Bitmap result = ImageMirror.make(
                ((BitmapDrawable)imageMain.getDrawable()).getBitmap());

        //loading
        if (result != null) {
            load(result);
        } else showOutOfMemoryToast();
    }

    //loading
    private void load(Bitmap result) {
        saveToHistory(result);
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.loading_row, null);
        progressList.addView(relativeLayout);

        //to real multithreading
        new ProgressTask(relativeLayout, rvAdapter, result)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //to one second thread
//        new ProgressTask(relativeLayout, rvAdapter, result).execute();
    }

    //Toasts
    private void showOutOfMemoryToast() {
        Toast.makeText(
                getApplicationContext(), R.string.toast_out_of_memory, Toast.LENGTH_SHORT).show();
    }

    private void showWrongUrlToast() {
        Toast.makeText(
                getApplicationContext(), R.string.toast_wrong_url, Toast.LENGTH_SHORT).show();
    }

    private void showHistoryToast() {
        Toast.makeText(
                getApplicationContext(), R.string.toast_history, Toast.LENGTH_SHORT).show();
    }

    private void showHaveNoCashesToast() {
        Toast.makeText(getApplicationContext(),
                R.string.toast_have_no_cashes, Toast.LENGTH_SHORT).show();
    }

    //history methods
    public void loadHistory(View view) {
        Set<String> imagesURI = preferences.getStringSet(
                KEY_IMAGES_URI_APP_PREFERENCES, new HashSet<String>());
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
            rvAdapter.addBitmapsList(bitmaps);
            showHistoryToast();
        } else {
            showHaveNoCashesToast();
        }
    }

    private void saveToHistory(Bitmap bitmap) {
        Set<String> imagesSet = preferences.getStringSet(
                KEY_IMAGES_URI_APP_PREFERENCES, new HashSet<String>());
        String uri = saveBitmapToSDCashes(bitmap);
        if (uri != null) {
            imagesSet.add(saveBitmapToSDCashes(bitmap));
            preferences.edit().putStringSet(KEY_IMAGES_URI_APP_PREFERENCES, imagesSet).apply();
        }
    }

    public String saveBitmapToSDCashes(Bitmap bitmap) {

        OutputStream outputStream = null;
        Time time = new Time();
        time.setToNow();

        //file name generation
        String name = Integer.toString(time.year)
                + Integer.toString(time.month)
                + Integer.toString(time.monthDay)
                + Integer.toString(time.hour)
                + Integer.toString(time.minute)
                + Integer.toString(time.second)
                +".jpg";

        try {
            File file = new File(this.getCacheDir(), name);

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
}
