package com.example.alex.testcft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
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
import android.widget.ProgressBar;

import com.example.alex.testcft.DataStructure.Process;
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
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //adapters
    private RVAdapter rvAdapter;

    //bitmap
    private Bitmap bitmap;

    //menu
    private android.support.v7.widget.PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRV();
    }

    private void initRV() {
        recyclerView = findViewById(R.id.resultListRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new RVAdapter(this);
        recyclerView.setAdapter(rvAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageMain.setImageBitmap(bitmap);
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
        popupMenu = new android.support.v7.widget.PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_rotate, popupMenu.getMenu());
        popupMenu.show();
    }

    public void openImageBrowser(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void rotate(MenuItem item) {
        int id = item.getItemId();
        final Process process;
        switch (id) {
            case R.id.menu_item_rotate_90_ckw:
                process = new Process(bitmap, Process.CODE_ROTATE_CKW);

                break;
            case R.id.menu_item_rotate_90_ccw:
                process = new Process(bitmap, Process.CODE_ROTATE_CCW);
                break;
            case R.id.menu_item_rotate_180:
                process = new Process(bitmap, Process.CODE_ROTATE_180);
                break;
            default: process = null;
            break;
        }

        final Context context = this;
        //loading

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressList.addView(progressBar);
        new ProgressTask(progressBar, rvAdapter, process);
    }
}
