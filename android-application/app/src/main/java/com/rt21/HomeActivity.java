package com.rt21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 103;
    private static final int PERMISSIONS_ALL = 1002;

    private MyApplication app;

    private TextView txtUsername;
    private TextView txtKilometerTraveled;
    private TextView txtSignsPassed;

    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtUsername = findViewById(R.id.txtHomeUsername);
        txtKilometerTraveled = findViewById(R.id.txtKilometersTraveled);
        txtSignsPassed = findViewById(R.id.txtSignsPassed);

        app = (MyApplication) getApplication();

        txtUsername.setText(app.testUser.getUsername());
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
                            Toast.makeText(this, "Please allow access to camera, location and storage!", Toast.LENGTH_SHORT).show();
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
}