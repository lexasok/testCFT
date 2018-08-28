package com.example.alex.testcft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.PopupMenu;

import com.example.alex.testcft.DataStructure.Process;
import com.example.alex.testcft.ImageProcessing.ImageRotater;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;

    //view
    private ConstraintLayout containerMain;
    private Button buttonChooseImage;
    private ImageView imageMain;
    private ImageView buttonRotate;

    //adapters
    private RVAdapter rvAdapter;

    //bitmap
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRV();

    }

    private void initRV() {
        RecyclerView recyclerView = findViewById(R.id.resultListRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new RVAdapter();
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
    }

    //show or hide views
    public void showContent() {
        if (buttonChooseImage.getVisibility() == View.VISIBLE) {
            buttonChooseImage.setVisibility(View.GONE);
            containerMain.setVisibility(View.VISIBLE);
        }
    }

    public void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
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
        Process process;
        switch (id) {
            case R.id.menu_item_rotate_90_ckw:
                process = new Process(bitmap, Process.CODE_ROTATE_CKW);
                rvAdapter.addProcess(process);
                break;
            case R.id.menu_item_rotate_90_ccw:
                process = new Process(bitmap, Process.CODE_ROTATE_CCW);
                rvAdapter.addProcess(process);
                break;
            case R.id.menu_item_rotate_180:
                process = new Process(bitmap, Process.CODE_ROTATE_180);
                rvAdapter.addProcess(process);
                break;
        }
    }
}
