package com.example.alex.testcft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.alex.testcft.HistoryManagment.HistoryLoader;
import com.example.alex.testcft.HistoryManagment.HistorySaver;
import com.example.alex.testcft.ImageProcessing.ImageMirror;
import com.example.alex.testcft.ImageProcessing.ImageRotate;
import com.example.alex.testcft.ImageProcessing.ProgressTask;
import com.example.alex.testcft.Model.ImageProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 2;
    private static final String KEY_EXTRAS_GET_PHOTO = "data";


    //views
    private ConstraintLayout containerContentMain;
    private Button buttonChooseImage;
    private ImageView imageMain;
    private LinearLayout progressList;
    private EditText urlInput;
    private ProgressBar urlLoadingProgress;
    private LinearLayout dialogContainer;

    //data
    private List<ImageProcessing> imageProcessings = new ArrayList<>();

    //adapters
    private RVAdapter rvAdapter;

    //Activity override methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRV();

        //restore data from app
        restoreImageProcesses();
        restoreResultData();
        restoreImageMain();

    }

    //get data methods
    private void restoreImageProcesses() {
        try {
        List<ImageProcessing> processes = getApp().getImageProcesses();
        if (processes.size() > 0) {
            for (ImageProcessing processing : processes) {
                load(processing.getResult());
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreResultData() {
        List<Bitmap> results = getApp().getResults();
        if (results.size() > 0) {
            rvAdapter.addBitmapsList(results);
        }
    }

    private void restoreImageMain() {
        Bitmap image = getApp().getImageMain();
        if (image != null) {
            imageMain.setImageBitmap(image);
            revertViewsVisibility();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                revertViewsVisibility();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageMain.setImageBitmap(bitmap);
                //save data
                getApp().setImageMain(bitmap);
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
            revertViewsVisibility();
            imageMain.setImageBitmap(imageBitmap);
            //save data
            getApp().setImageMain(imageBitmap);
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

    //app
    public AppCFT getApp() {
        return (AppCFT) getApplication();
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

        revertViewsVisibility();
    }

    private void revertViewsVisibility() {
        if (dialogContainer == null) {
            containerContentMain.setVisibility(View.VISIBLE);
            buttonChooseImage.setVisibility(View.GONE);
        } else if (dialogContainer.getVisibility() == View.GONE) {
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
                        revertViewsVisibility();
                        //save data
                        getApp().setImageMain(((BitmapDrawable) resource).getBitmap());
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
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.loading_row, null);
        progressList.addView(relativeLayout);

        //to real multithreading
        HistorySaver historySaver = new HistorySaver(getApplicationContext());
        new ProgressTask(relativeLayout, rvAdapter, result, historySaver, getApp())
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

    //history methods
    public void loadHistory(View view) {

        HistoryLoader historyLoader = new HistoryLoader(getApplicationContext(), rvAdapter);
        historyLoader.load();
    }
}
