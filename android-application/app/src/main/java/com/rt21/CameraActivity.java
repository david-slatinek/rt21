package com.rt21;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.koushikdutta.ion.Ion;
import com.rt21.data.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;


public class CameraActivity extends AppCompatActivity {

    private MyApplication app;

    private boolean driving = false;
    private TextView txtLocation;
    private TextureView textureViewCameraFlowPreview;
    private Button buttonTakePicture;
    private Button btnStartDrive;
    private ImageView imageViewCapturedPhoto;

    private MapView mapView;
    private MapController mapController;
    private double lat = 46.55898260175286;
    private double lng = 15.637994971467204;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int ACTIVITY_ID = 102;

    public static final String IMAGE_RESOLUTION = "IMAGE_RESOLUTION";
    public static final int IMAGE240p = 240;
    public static final int IMAGE480p = 480;
    public static final int IMAGE720p = 720;
    public static final int IMAGE768p = 768;
    public static final int IMAGE1080p = 1080;

    private Size imageResolutionFinal;


    // handler will be used to trigger code to take a picture every X seconds
    Handler myHandler;
    // runnable stores the code that will execute every x seconds
    Runnable myRunnable;
    // timer is set to 5 seconds
    int delayMilliSeconds = 5000;


    // Google's interface that consumes less energy because it is optimized
    // It uses Google Play Services
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** icon on map that will show device's current location**/
    Marker currentLocationMarker;

        LocationRequest locationRequest;

    // here we can get location every X seconds (depends of settings in locationSettings method)
    private LocationCallback locationCallback = new LocationCallback() {
        // waits when location is returned
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            if(locationResult == null){
                // result was empty. Exit method
                return;
            }

            // for each returned location: show it on map and put to log
            for(Location location : locationResult.getLocations()){
                showOnMap(location);
                // Log.d("location", "onLocationResult: " + location.toString());
            }
        }
    };

    public int LOCATION_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        app = (MyApplication) getApplication();

        txtLocation = findViewById(R.id.txtLocationHolder);
        textureViewCameraFlowPreview = (TextureView) findViewById(R.id.textureViewCameraPreview);
        buttonTakePicture = findViewById(R.id.buttonTakePicture);
        imageViewCapturedPhoto = findViewById(R.id.imageView);
        btnStartDrive = findViewById(R.id.btnStartDrive);

        btnStartDrive.setText("Start drive");


        Configuration.getInstance().setUserAgentValue(getPackageName());

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        mapView.setMultiTouchControls(true);

        mapController = (MapController) mapView.getController();
        mapController.setZoom(13);

        //TODO - get current location
        //TODO - realtime get location attributes -> onLocationChanged()
        GeoPoint gPt = new GeoPoint(lat, lng);
        mapController.setCenter(gPt);

        //set resolution of photo from value which was passed in intent
        imageResolutionFinal = getImageResolution(getIntent().getExtras().getInt(IMAGE_RESOLUTION));

        // when activity starts begin with camera preview
        startCameraFlow();

        // put marker on map
        initializeLocationMarker();

        // create new object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // set interval and precision of location
        locationSettings();

    }

    private void locationSettings(){
        // create new subscriber for location
        locationRequest = LocationRequest.create();

        // every 10 seconds ask for location
        locationRequest.setInterval(10000);

        // if location is being retreived by another application at the same time
        // interval be lowered to as little as 5 seconds but no less
        locationRequest.setFastestInterval(5000);

        // we need high precision
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    @Override
    protected void onStop() {
        super.onStop();

        // when app stops stop listening for location
        stopLocationUpdates();
    }

    // start location updates
    @SuppressLint("MissingPermission")
    private void startLocationUpdate(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    // stop location update
    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    /**
     * @param resolution constant of image size. Class CameraActivity.IMAGE___p
     * @return Object Size(width, height) of image
     */
    // returns Size object
    public Size getImageResolution(int resolution) {
        switch (resolution) {
            case IMAGE240p:
                return new Size(240, 320);

            case IMAGE480p:
                return new Size(480, 640);

            case IMAGE720p:
                return new Size(720, 960);

            case IMAGE768p:
                return new Size(768, 1024);

            case IMAGE1080p:
                return new Size(1080, 1440);

            // if none of constants is matching with resolution parameter pick a default value
            default:
                return new Size(192, 256);
        }
    }

    public void startCameraFlow() {
        // close other instances if they use camera at this moment
        CameraX.unbindAll();

        // get aspect ratio of field where camera flow will be shown
        Rational aspectRatio = new Rational(textureViewCameraFlowPreview.getWidth(), textureViewCameraFlowPreview.getHeight());

        // get size of field where camera flow will be shown
        Size screen = new Size(textureViewCameraFlowPreview.getWidth(), textureViewCameraFlowPreview.getHeight());

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
                .setFlashMode(FlashMode.OFF).setTargetResolution(imageResolutionFinal).build();

        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        // when button is pressed take a picture and save it
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create new folder in application storage
                File direcoryPictures = new File(getFilesDir().getAbsolutePath() + File.separator + "Pictures");
                if (!direcoryPictures.exists())
                    direcoryPictures.mkdirs();


                // create new file. name is from current time
                File file = new File(direcoryPictures, app.user.getId() + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                // take a picture from camera and save it to file and check if it was saved
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    // image was successfully saved to file
                    public void onImageSaved(@NonNull @NotNull File file) {
                        // notify user where image was saved with absolute path

                        //TODO - remove message when application is finished
                        CommonMethods.displayToastShort("Image was captured and saved in: " + file.getAbsolutePath(), getApplicationContext());

                        // read that file and show it in imageView with Bitmap object
                        Bitmap original = BitmapFactory.decodeFile(file.getAbsolutePath());

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

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
        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imageCapture);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void startDriving(){

        // Inform user that the driving has started
        CommonMethods.displayToastShort("Driving started", this);

        // start receiving location
        startLocationUpdate();

        buttonTakePicture.performClick();
        // every x seconds execute code in run function
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable = new Runnable() {
            public void run() {
                myHandler.postDelayed(myRunnable, delayMilliSeconds);
                // simulate user click on button
                buttonTakePicture.performClick();
            }
        }, delayMilliSeconds);
    }

    private void stopDriving(){

        // create entry in database
        createNewDrive();

        // Inform user that the driving has stopped
        CommonMethods.displayToastShort("Driving stopped", this);

        // stop receiving location
        stopLocationUpdates();

        // stop handler from calling clicks on button
        myHandler.removeCallbacks(myRunnable);
        myHandler.removeCallbacksAndMessages(null);
        // set both handler and runnable to null
        myHandler = null;
        myRunnable = null;
    }


    // when this method is called every x seconds new image will be taken
    public void onClickEnableTimerToTakeImageAndLocation(View view) {
        btnStartDrive.setText(!driving ? "Stop drive" : "Start drive");
        // simulate user click on button
        driving = !driving;

        if (driving) {
            startDriving();
        }else {
            stopDriving();
        }
    }





    private void showOnMap(Location location){
        // mapcontroller controls map layout
        mapController = (MapController) mapView.getController();

        // zoom to
        mapController.setZoom(18.5);

        // retrieve latitude and longitude from Location and save it to GeoPoint
        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        // move map so the geopoint is on center of map
        mapController.setCenter(currentLocation);

        // move marker to GeoPoint location on map
        currentLocationMarker.setPosition(currentLocation);

        // write coordinates on display
        txtLocation.setText(String.format("Longitude: %s\t\nLatitude: %s", location.getLongitude(), location.getLatitude()));
    }

    private void initializeLocationMarker(){
        currentLocationMarker = new Marker(mapView);
        currentLocationMarker.setTitle("My location");
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentLocationMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_found));
        mapView.getOverlays().add(currentLocationMarker);
    }



    private boolean createNewDrive() {
        try {
            JsonObject json = Ion.with(getBaseContext())
                    .load("POST", "https://rt21-api.herokuapp.com/api/drive/create_drive")
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("user_id", app.user.getId())
                    .asJsonObject()
                    .get();

            JSONObject jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("error", getApplicationContext());
                return false;
            } else {
                CommonMethods.displayToastShort("New drive on database was created", getApplicationContext());

                JSONObject json_id = jsonObject.getJSONObject("_id");
                String _id = json_id.getString("$oid");

                Log.d("loca", "id of drive: " + _id);
                Timber.i(app.user.toString());
                return true;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

}