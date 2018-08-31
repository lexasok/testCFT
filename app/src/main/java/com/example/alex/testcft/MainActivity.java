package com.example.alex.testcft;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.example.alex.testcft.Model.Data;

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
    private List<Bitmap> processingResult = new ArrayList<>();
    private List<Bitmap> mData = new ArrayList<>();

    //adapters
    private RVAdapter rvAdapter;

    //Activity override methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();

        initViews();
        initRV();

        Data data = (Data) getLastCustomNonConfigurationInstance();
        if (data != null) {
            restoreProcessingResult(data);
            restoreImageMain(data);
            restoreResultData(data);
            showContent();
        }
    }

    private void setLayout() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    //restore data methods
    private void restoreProcessingResult(Data data) {
        if (data.getmProcesses() != null && data.getmProcesses().size() > 0) {
            List<Bitmap> processesToStart = new ArrayList<>(data.getmProcesses());
            for (Bitmap result : processesToStart) {
                load(result);
            }
        }
    }

    private void restoreResultData(Data data) {
        mData = data.getmData();
        if (mData.size() > 0) {
            rvAdapter.addBitmapsList(mData);
        }
    }

    private void restoreImageMain(Data data) {
        Bitmap image = data.getmImageMain();
        if (image != null) {
            imageMain.setImageBitmap(image);
        }
    }

    //Activity methods
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            revertViewsVisibility();
            imageMain.setImageBitmap(bitmap);
//            showContent();
        }

        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get(KEY_EXTRAS_GET_PHOTO);
            revertViewsVisibility();
            imageMain.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        try {
            Bitmap bitmap = ((BitmapDrawable) imageMain.getDrawable()).getBitmap();
            return new Data(bitmap, rvAdapter.getData(), processingResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public void loadFromURL(View view) {
        String query = urlInput.getText().toString();
        if (!TextUtils.isEmpty(query)) {
            urlLoadingProgress.setVisibility(View.VISIBLE);
            Glide
                    .with(getApplicationContext())
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
                            return false;
                        }
                    })
                    .into(imageMain);
        }
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

        //save result to history
        HistorySaver historySaver = new HistorySaver(getApplicationContext());

        //process count
        processingResult.add(result);
        //to real multithreading
        new ProgressTask(relativeLayout, rvAdapter, result, historySaver, this)
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

    //data (instance) method
    public void minusProcessingResult() {
        if (processingResult.size() > 0) {
            processingResult.remove(processingResult.size() - 1);
        }
    }

}
