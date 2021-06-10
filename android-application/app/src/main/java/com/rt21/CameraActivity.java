package com.rt21;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.koushikdutta.ion.Ion;
import com.google.gson.JsonObject;

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
import java.util.Random;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;


public class CameraActivity extends AppCompatActivity {

    private MyApplication app;

    private boolean driving = false;
    private TextureView textureViewCameraFlowPreview;
    private Button buttonTakePicture;
    private Button btnStartDrive;
    private ImageView imageViewCapturedPhoto;

    private MapView mapView;
    private MapController mapController;
    private double oldLat = 0;
    private double oldLng = 0;
    private double lat = 46.55898260175286;
    private double lng = 15.637994971467204;
    private double maxSpeed = 0;
    private double avgSpeed = 0;
    private int speedChek = 0;
    private int numOfStops = 0;
    private double lenghtOfDrive = 0;

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

    // handler will be used to trigger code to change checkSensor to true
    Handler myHandlerSensor;
    // runnable stores the code that will execute every x seconds
    Runnable myRunnableSensor;


    // Google's interface that consumes less energy because it is optimized
    // It uses Google Play Services
    private FusedLocationProviderClient fusedLocationProviderClient;

    /** icon on map that will show device's current location**/
    Marker currentLocationMarker;

    LocationRequest locationRequest;

    // here is saved the most recent location
    Location mostRecentLocation = null;

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
                sendLocation(location);
                mostRecentLocation = location;
                buttonTakePicture.performClick();
                // Log.d("location", "onLocationResult: " + location.toString());
            }
        }
    };

    public int LOCATION_REQUEST_CODE = 10001;


    // sensor manager
    private SensorManager sensorManager;
    private float acelVal, acelLast, shake;


    // array to store values from sensor
    private static float[] arrayOfShakeValues;
    // size of array
    private static final int sizeOfArrayOfShakeValues = 20;
    // value that tells if onSensorChanged handler should write down the value or not
    private static boolean checkSensor;
    // index that tells where to write data in array
    private static int counterSensorArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        app = (MyApplication) getApplication();

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

        // assign system services to sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // subscribe to changes from accelerometer
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


        acelVal=SensorManager.GRAVITY_EARTH;
        acelLast=SensorManager.GRAVITY_EARTH;
        shake=0.000f;

        // create new float array
        arrayOfShakeValues = new float[sizeOfArrayOfShakeValues];
        //for start set sensorCheck to true
        checkSensor = true;
        // index of array set to 0
        counterSensorArray = 0;

        // every x seconds execute code in run function
        myHandlerSensor = new Handler();
        myHandlerSensor.postDelayed(myRunnableSensor = new Runnable() {
            public void run() {
                myHandlerSensor.postDelayed(myRunnableSensor, 500);
                // set check to true every half a second
                checkSensor = true;
            }
        }, 500);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void locationSettings(){
        // create new subscriber for location
        locationRequest = LocationRequest.create();

        // every 10 seconds ask for location
        locationRequest.setInterval(10000);

        // if location is being retreived by another application at the same time
        // interval be lowered to as little as 5 seconds but no less
        //locationRequest.setFastestInterval(5000);

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

                // store location that was provided at the time image was taken
                Location locationWhereImageWasCaptured = mostRecentLocation;
                // create new folder in application storage
                File direcoryPictures = new File(getFilesDir().getAbsolutePath() + File.separator + "Pictures");
                if (!direcoryPictures.exists())
                    direcoryPictures.mkdirs();


                // create new file. name is from current time
                    File fileImage = new File(direcoryPictures, app.user.getId() + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                // take a picture from camera and save it to file and check if it was saved
                imageCapture.takePicture(fileImage, new ImageCapture.OnImageSavedListener() {
                    @Override
                    // image was successfully saved to file
                    public void onImageSaved(@NonNull @NotNull File file) {
                        // notify user where image was saved with absolute path

                        // toast user that image was saved
                        //CommonMethods.displayToastShort("Image was captured and saved in: " + file.getAbsolutePath(), getApplicationContext());
                        Log.d("location", "Image was captured and saved in: " + file.getAbsolutePath());

                        // read that file and show it in imageView with Bitmap object
                        Bitmap original = BitmapFactory.decodeFile(file.getAbsolutePath());

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

                        imageViewCapturedPhoto.setImageBitmap(rotated);

                        // image is saved on phone now send it to server and wait for response
                        try {
                            JsonObject json = Ion.with(getBaseContext())
                                    .load("POST", "https://rt21-api.herokuapp.com/api/sign/recognize")
                                    .setHeader(app.getKeyName(), app.getApiKey())
                                    // add image to api post request
                                    .setMultipartFile("image", file)
                                    .asJsonObject()
                                    .get();

                            JSONObject jsonObject = new JSONObject(json.toString());
                            if (jsonObject.has("error")) {
                                // image was not sent correctly (e.g. wrong file extension)
                                Log.d("sendImage", "Image was not sent");
                            } else {
                                // no error

                                // check if returned object has sign_type field
//                                if (jsonObject.has("sign_type"))
//                                    Log.d("getSign", "api returned sign");

                                // get sign from returned json object
                                String signType = jsonObject.getString("sign_type");

                                // write to log which sign it was
                                Log.d("getSign", "sign: " + signType);


                            }
                            // if something went wrong
                        } catch (ExecutionException | InterruptedException | JSONException e) {
                            Timber.i("JSON parsing error: %s", e.getMessage());
                            e.printStackTrace();
                        }


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

    }

    private void stopDriving(){

        // Inform user that the driving has stopped
        CommonMethods.displayToastShort("Driving stopped", this);

        // stop receiving location
        stopLocationUpdates();

        try {
            // update length of drive
            double roundedLen = Math.round((double)(lenghtOfDrive / 1000) * 1000) / 1000.0;
            JsonObject json = Ion.with(getBaseContext())
                    .load("PUT", "https://rt21-api.herokuapp.com/api/drive/" + app.driveID)
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("key", "length")
                    .setBodyParameter("value", String.valueOf(roundedLen))
                    .asJsonObject()
                    .get();
            JSONObject jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("Error: " + jsonObject.getString("error"), getApplicationContext());
            }

            // update max speed
            double roundedMaxSpeed = Math.round(maxSpeed * 100) / 100.0;
            json = Ion.with(getBaseContext())
                    .load("PUT", "https://rt21-api.herokuapp.com/api/drive/" + app.driveID)
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("key", "max_speed")
                    .setBodyParameter("value", String.valueOf(roundedMaxSpeed))
                    .asJsonObject()
                    .get();
            jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("Error: " + jsonObject.getString("error"), getApplicationContext());
            }

            // update avg speed
            double roundedAvgSpeed = Math.round((avgSpeed / speedChek) * 100) / 100.0;
            json = Ion.with(getBaseContext())
                    .load("PUT", "https://rt21-api.herokuapp.com/api/drive/" + app.driveID)
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("key", "mean_speed")
                    .setBodyParameter("value", String.valueOf(roundedAvgSpeed))
                    .asJsonObject()
                    .get();
            jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("Error: " + jsonObject.getString("error"), getApplicationContext());
            }

            // update number of stops
            json = Ion.with(getBaseContext())
                    .load("PUT", "https://rt21-api.herokuapp.com/api/drive/" + app.driveID)
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("key", "nr_of_stops")
                    .setBodyParameter("value", String.valueOf(numOfStops))
                    .asJsonObject()
                    .get();
            jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("Error: " + jsonObject.getString("error"), getApplicationContext());
            }

            CommonMethods.displayToastShort("Length: "+ roundedLen +"\nMaxSpeed: "+ roundedMaxSpeed +"\nAverage speed: "+ roundedAvgSpeed +"\nNr of stops: "+ numOfStops, getApplicationContext());

        } catch (ExecutionException | InterruptedException | JSONException e) {
            CommonMethods.displayToastShort("Error: " + e, getApplicationContext());
            Timber.i("Napaka json: %s", e.getMessage());
            e.printStackTrace();
        }

        app.driveID = "";
    }


    // when this method is called every x seconds new image will be taken
    public void onClickEnableTimerToTakeImageAndLocation(View view) {

        if(!driving) {
            try {
                JsonObject json = Ion.with(getBaseContext())
                        .load("POST", "https://rt21-api.herokuapp.com/api/drive/create")
                        .setHeader(app.getKeyName(), app.getApiKey())
                        .setBodyParameter("user_id", app.user.getId())
                        .asJsonObject()
                        .get();

                JSONObject jsonObject = new JSONObject(json.toString());
                if (jsonObject.has("error")) {
                    CommonMethods.displayToastShort("error", getApplicationContext());
                } else {
                    app.driveID = jsonObject.getString("$oid");
                }
            } catch (ExecutionException | InterruptedException | JSONException e) {
                Timber.i("Napaka json: %s", e.getMessage());
                e.printStackTrace();
            }
        }

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
    }

    private void initializeLocationMarker(){
        currentLocationMarker = new Marker(mapView);
        currentLocationMarker.setTitle("My location");
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentLocationMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_found));
        mapView.getOverlays().add(currentLocationMarker);
    }


    private void sendLocation(Location location){

        // save road quality to string. 'java.util.Locale.US,"%.1f"' forces only one decimal number from float and dot (.) as decimal separator.
        // getAverageOfShakes() returns average value from array and getRoadQualityFromAverage inverts it (1 to 10 and 9.2 to 1.8 ...)
        String roadQuality = String.valueOf(String.format(java.util.Locale.US,"%.1f", getRoadQualityFromAverage(getAverageOfShakes())));
        //TODO: remove message when application is finished
        CommonMethods.displayToastShort("trying to send location, quality is: " + roadQuality, this);

        // check if drive on database was created previously
        if (app.driveID == null) {
            // we dont have drive_id so we cant assign location to it.
            CommonMethods.displayToastShort("location wont be send", this);
            return;
        }
        try {
            //convert from m/s to km/h
            double speed = location.getSpeed() * 3.6;

            //check for full stops
            if (speed < 0.01) {
                numOfStops++;
                CommonMethods.displayToastShort("Stops: " + numOfStops, this);

            } else {
                //calculate avg speed
                speedChek++;
                avgSpeed += speed;
            }
            //tracking avg speed
            if (maxSpeed < speed) {
                maxSpeed = speed;
            }

            // calculate distance traveled
            if (oldLng != 0 && oldLat != 0) {
                Location tmp = new Location("prev");
                tmp.setLatitude(oldLat);
                tmp.setLongitude(oldLng);
                lenghtOfDrive += location.distanceTo(tmp);
            }

            oldLat = location.getLatitude();
            oldLng = location.getLongitude();

            JsonObject json = Ion.with(getBaseContext())
                    .load("POST", "https://rt21-api.herokuapp.com/api/location/create")
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("drive_id", app.driveID)
                    .setBodyParameter("latitude", String.valueOf(location.getLatitude()))
                    .setBodyParameter("longitude", String.valueOf(location.getLongitude()))
                    .setBodyParameter("road_quality", roadQuality)
                    .asJsonObject()
                    .get();

            JSONObject jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("error", getApplicationContext());
            } else {
                CommonMethods.displayToastShort("location was sent", this);
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }


    // event listener
    private final SensorEventListener sensorListener = new SensorEventListener() {

        //when sensor changes state run this
        @Override
        public void onSensorChanged(SensorEvent event) {

            // run only if x amount of time is passed so the checkSensor was ticked to true
            if(checkSensor) {

                // if array is about to be full start overwriting it at the beginning so set counter to 0
                if(counterSensorArray >= sizeOfArrayOfShakeValues)
                    counterSensorArray = 0;

                // set to false (handlerSensor will turn it on again after x seconds)
                checkSensor = false;

                // get xyz values
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                acelLast=acelVal;
                acelVal = (float) Math.sqrt((double) (x * x) + (y * y) + (z * z));
                float delta = acelVal - acelLast;
                shake = shake * 0.9f + delta;

                // if shake is negative multiply it with -1 to change it to positive
                if(shake < 0)
                    shake *= -1;

                // write current shake value to array at index counterSensorArray
                arrayOfShakeValues[counterSensorArray++] = shake;
            }
        }

        // we don't need this method for now but it can't be deleted
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private float getAverageOfShakes(){

        float sumOfArray = 0.0f;

        // sum all entries in array
        for(float f : arrayOfShakeValues){
            sumOfArray += f;
        }

        // divide sum of array with number of elements in array
        float average =  (sumOfArray / sizeOfArrayOfShakeValues);

        // if value is bigger than 10.0 return 10.0
        if(average > 10.0f)
            return 10.0f;

        // if value is smaller than 1.0 return 1.0
        if (average < 1.0 )
            return 1.0f;

        // return average
        return average;
    }

    private float getRoadQualityFromAverage(float average){
        // multiply value with 10 and cast it to integer
        int averageX10 = (int) (average * 10);
        int invertedAverage;

        // get inverted value by subtracting current average from sum of minimum and maximum value (10 + 100 - X = 110 - X)
        invertedAverage = 55 - (averageX10 - 55);

        // return quality divided by 10.0 so we get float type (e.g. 4.2)
        return (invertedAverage / 10.0f);
    }
}