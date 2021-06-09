package com.rt21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class HomeActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 103;
    private static final int PERMISSIONS_ALL = 1002;

    private MyApplication app;

    private TextView txtKilometerTraveled;
    private TextView txtMaxSpeed;
    private TextView txtAvgSpeed;


    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtKilometerTraveled = findViewById(R.id.txtKilometersTraveled);
        txtMaxSpeed = findViewById(R.id.txtMaxSpeed);
        txtAvgSpeed = findViewById(R.id.txtAvgSpeed);

        app = (MyApplication) getApplication();

        double sumLen = 0;
        double maxSpeed = 0;
        double avgSpeed = 0;
        int numOfDrives = 0;

        try {
            JsonObject json = Ion.with(getBaseContext())
                    .load("GET", "https://rt21-api.herokuapp.com/api/drive/getDrives/" + app.user.getId())
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .asJsonObject()
                    .get();
            JSONObject jsonObject = new JSONObject(json.toString());

            if (jsonObject.has("error")) {
                CommonMethods.displayToastShort("Error", this);
            } else {
                for (int i = 0; i < jsonObject.length(); i++)  {
                    JSONObject tmp = jsonObject.getJSONObject(String.valueOf(i));

                    sumLen += Double.parseDouble(tmp.getString("length"));
                    avgSpeed += Double.parseDouble(tmp.getString("mean_speed"));
                    double tmpMax = Double.parseDouble(tmp.getString("max_speed"));
                    if (tmpMax >maxSpeed) {
                        maxSpeed = tmpMax;
                    }
                    numOfDrives++;
                }
            }

        } catch (JSONException | ExecutionException | InterruptedException e) {
            Timber.i("My error: %s", e.getMessage());
            e.printStackTrace();
        }


        txtKilometerTraveled.setText(String.format(getString(R.string.distance_traveled_format), Double.toString(sumLen)));
        txtMaxSpeed.setText(String.format(getString(R.string.road_signs_passed_format), Double.toString(maxSpeed)));
        txtAvgSpeed.setText(String.format(getString(R.string.average_speed), Double.toString(Math.round((avgSpeed / numOfDrives) * 100) / 100.0)));
    }

    public void onClickOpenCamera(View view) {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ALL);
        } else {
            Intent i = new Intent(getBaseContext(), CameraActivity.class);
            i.putExtra(CameraActivity.IMAGE_RESOLUTION, CameraActivity.IMAGE240p);
            startActivity(i);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_ALL: {
                if (grantResults.length >= PERMISSIONS.length) {
                    for (int i = 0; i < PERMISSIONS.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            CommonMethods.displayToastShort("Please allow access to camera and location!", getApplicationContext());
                            return;
                        }
                    }
                    Intent i = new Intent(getBaseContext(), CameraActivity.class);
                    startActivity(i);
                } else {
                    return;
                }
            }
        }
    }

    public void onClickLogOut(View view) {
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sprefs.edit();
        editor.remove("remember_user");
        editor.remove("_id");
        editor.remove("fullname");
        editor.remove("email");
        editor.remove("age");
        editor.remove("username");
        editor.apply();


        Intent i = getIntent();
        i.putExtra("sign_out", true);
        setResult(RESULT_OK, i);
        finish();
    }

    public void onClickOpenProfile(View view) {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
    }
}