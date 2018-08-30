package com.example.alex.testcft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.alex.testcft.ImageProcessing.ImageMirror;
import com.example.alex.testcft.ImageProcessing.ImageRotate;
import com.example.alex.testcft.ImageProcessing.ProgressTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 2;
    private static final String KEY_EXTRAS_GET_PHOTO = "data";

    //view
    private ConstraintLayout containerContentMain;
    private Button buttonChooseImage;
    private ImageView imageMain;
    private LinearLayout progressList;
    private EditText urlInput;
    private ProgressBar urlLoadingProgress;
    private LinearLayout dialogContainer;

    //adapters
    private RVAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRV();
    }

    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.resultListRV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new RVAdapter(this, imageMain);
        recyclerView.setAdapter(rvAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
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
            imageMain.setImageBitmap(imageBitmap);
        }
    }

    private void initViews() {
        containerContentMain = findViewById(R.id.containerContentMain);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        imageMain = findViewById(R.id.imageMain);
        //buttonRotate = findViewById(R.id.buttonRotate);
        progressList = findViewById(R.id.progressList);
        urlLoadingProgress = findViewById(R.id.urlLoadingProgressBar);
    }

    //show or hide views
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


    //init buttons methods
    public void openImageBrowser(View view) {
        revertViewsByDialog();
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void openCamera(View view) {
        revertViewsByDialog();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_FROM_CAMERA);
        }
    }


    public void rotate(MenuItem item) {
        System.gc();
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
        System.gc();

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
        System.gc();
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
        new ProgressTask(relativeLayout, rvAdapter, result)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //to one second thread
//        new ProgressTask(relativeLayout, rvAdapter, result).execute();
    }

    public void setImageMain(Bitmap bitmap) {
        imageMain.setImageBitmap(bitmap);
    }

    public void showOutOfMemoryToast() {
        Toast.makeText(
                getApplicationContext(), R.string.toast_out_of_memory, Toast.LENGTH_SHORT).show();
    }

    public void showWrongUrlToast() {
        Toast.makeText(
                getApplicationContext(), R.string.toast_wrong_url, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrimMemory(int level) {
        System.gc();
        super.onTrimMemory(level);
    }

    public void showDialog(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.dialog_loading_image, (ViewGroup) findViewById(R.id.containerMain));
        urlInput = layout.findViewById(R.id.dialogLoadFromURLEditText);
        dialogContainer = layout.findViewById(R.id.loadImageDialogContainer);

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

    public void loadFromURL(String query) {
        urlLoadingProgress.setVisibility(View.VISIBLE);
        revertViewsByDialog();
        Glide
                .with(this)
                .load(query)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model
                            , Target<Drawable> target, boolean isFirstResource) {
                        urlLoadingProgress.setVisibility(View.GONE);
                        revertViewsByDialog();
                        showWrongUrlToast();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model
                            , Target<Drawable> target, DataSource dataSource
                            , boolean isFirstResource) {
                        urlLoadingProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageMain);
    }
}
