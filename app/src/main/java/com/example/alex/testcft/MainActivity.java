package com.example.alex.testcft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //constants
    private static final int RESULT_LOAD_IMAGE = 1;

    //fields
    private ConstraintLayout containerMain;
    private Button buttonChooseImage;
    private ImageView imageMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init main container
        containerMain = findViewById(R.id.containerContentMain);

        //init loading image OnClickListener
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        };

        //first loading image
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        buttonChooseImage.setOnClickListener(onClickListener);

        //init loading image
        imageMain = findViewById(R.id.imageMain);
        //choosing image
        imageMain.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageMain.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            showContent();
        }
    }

    //show or hide views
    private void showContent() {
        if (buttonChooseImage.getVisibility() == View.VISIBLE) {
            buttonChooseImage.setVisibility(View.GONE);
            containerMain.setVisibility(View.VISIBLE);
        }
    }
}
