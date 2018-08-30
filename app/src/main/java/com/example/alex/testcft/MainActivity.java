package com.example.alex.testcft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alex.testcft.ImageProcessing.BlackAndWhiteImage;
import com.example.alex.testcft.ImageProcessing.ImageMirror;
import com.example.alex.testcft.ImageProcessing.ImageRotate;
import com.example.alex.testcft.ImageProcessing.ProgressTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;

    //view
    private ConstraintLayout containerMain;
    private Button buttonChooseImage;
    private ImageView imageMain;
    private ImageView buttonRotate;
    private LinearLayout progressList;

    //adapters
    private RVAdapter rvAdapter;

    //bitmap
//    private Bitmap bitmap;

    //Context
    private Context mContext;

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
                imageMain.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

            showContent();
        }
    }

    private void initViews() {
        containerMain = findViewById(R.id.containerContentMain);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        imageMain = findViewById(R.id.imageMain);
        buttonRotate = findViewById(R.id.buttonRotate);
        progressList = findViewById(R.id.progressList);
    }

    //show or hide views
    public void showContent() {
        if (buttonChooseImage.getVisibility() == View.VISIBLE) {
            buttonChooseImage.setVisibility(View.GONE);
            containerMain.setVisibility(View.VISIBLE);
        }
    }

    public void showPopup(View view) {
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_rotate, popupMenu.getMenu());
        popupMenu.show();
    }


    //init buttons methods
    public void openImageBrowser(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_row, null);
        progressList.addView(relativeLayout);

        //to real multithreading
        new ProgressTask(relativeLayout, rvAdapter, result).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        System.gc();
    }
}
