package com.rt21;

// activity to test Camera class

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public class CameraActivity extends AppCompatActivity {


    TextureView textureViewCameraFlowPreview;
    Button buttonTakePicture;
    ImageView imageViewCapturedPhoto;



    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int ACTIVITY_ID = 102;

    // handler will be used to trigger code to take a picture every X seconds
    Handler myHandler = new Handler();
    // runnable stores the code that will execute every x seconds
    Runnable myRunnable;
    // timer is set to 5 seconds
    int delayMilliSeconds = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureViewCameraFlowPreview = (TextureView)findViewById(R.id.textureViewCameraPreview);
        buttonTakePicture = findViewById(R.id.buttonTakePicture);
        imageViewCapturedPhoto = findViewById(R.id.imageView);

        // when activity starts begin with camera preview
        startCameraFlow();
    }

    public void startCameraFlow(){
        // close other instances if they use camera at this moment
        CameraX.unbindAll();

        // get aspect ratio of field where camera flow will be shown
        Rational aspectRatio = new Rational(textureViewCameraFlowPreview.getWidth(), textureViewCameraFlowPreview.getHeight());

        // get size of field where camera flow will be shown
        Size screen = new Size(textureViewCameraFlowPreview.getWidth(),  textureViewCameraFlowPreview.getHeight());

        // object which holds configuration (aspect ratio and size) for preview.
        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();

        // new object for previewing camera flow
        Preview preview = new Preview(previewConfig);

        // update preview on change (when new frame is passed from camera flow)
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                // get textureview parent
                ViewGroup parent = (ViewGroup) textureViewCameraFlowPreview.getParent();

                // remove frame from view by removing whole View
                parent.removeView(textureViewCameraFlowPreview);

                // add new view which does NOT yet contain new frame from camera flow
                parent.addView(textureViewCameraFlowPreview, 0);

                // output from preview (camera flow) is saved to textureView with setSurfaceTexture
                textureViewCameraFlowPreview.setSurfaceTexture(output.getSurfaceTexture());
            }
        });

        // configuration for camera. ImageCapture.CaptureMode.MIN_LATENCY prioritizes speed over quality of photo
        // rotation of the image is set the same as device's current rotation
        // flash is turned off and desired resolution is set to 1280x720. cameraX takes the photo of nearest possible resolution
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .setFlashMode(FlashMode.OFF).setTargetResolution(new Size(1280, 720)).build();

        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        // when button is pressed take a picture and save it
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create new file. name is from current time
                File file = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");

                // take a picture from camera and save it to file and check if it was saved
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    // image was successfully saved to file
                    public void onImageSaved(@NonNull @NotNull File file) {
                        // notify user where image was saved with absolute path

                        CommonMethods.displayToastShort("Image was captured and saved in: " + file.getAbsolutePath(), getApplicationContext());

                        // read that file and show it in imageView with Bitmap object
                        Bitmap original = BitmapFactory.decodeFile(file.getAbsolutePath());

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotated = Bitmap.createBitmap(original, 0,0, original.getWidth(), original.getHeight(), matrix, true);

                        imageViewCapturedPhoto.setImageBitmap(rotated);
                    }

                    @Override
                    // image was NOT successfully saved to file
                    public void onError(@NonNull @NotNull ImageCapture.UseCaseError useCaseError, @NonNull @NotNull String message, @Nullable @org.jetbrains.annotations.Nullable Throwable cause) {
                        // notify user what went wrong
                        CommonMethods.displayToastShort("Image was NOT captured and saved!", getApplicationContext());

                    }
                });
            }
        });

        // initialize camera again to life cycle owner after image was captured
        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imageCapture);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    // when this method is called every x seconds new image will be taken
    public void onClickEnableTimerToTakeImage(View view) {
        // simulate user click on button
        buttonTakePicture.performClick();
        // every x seconds execute code in run function
        myHandler.postDelayed(myRunnable = new Runnable() {
            public void run() {
                myHandler.postDelayed(myRunnable, delayMilliSeconds);
                // simulate user click on button
                buttonTakePicture.performClick();
            }
        }, delayMilliSeconds);
    }
}