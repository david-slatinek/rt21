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
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 103;
    private static final int PERMISSIONS_ALL = 1002;

    private MyApplication app;

    private TextView txtKilometerTraveled;
    private TextView txtSignsPassed;

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
        txtSignsPassed = findViewById(R.id.txtSignsPassed);

        app = (MyApplication) getApplication();

        txtKilometerTraveled.setText(String.format(getString(R.string.distance_traveled_format), Integer.toString(app.distance)));
        txtSignsPassed.setText(String.format(getString(R.string.road_signs_passed_format), Integer.toString(app.signs)));
    }

    public void onClickOpenCamera(View view) {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ALL);
        } else {
            Intent i = new Intent(getBaseContext(), CameraActivity.class);
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