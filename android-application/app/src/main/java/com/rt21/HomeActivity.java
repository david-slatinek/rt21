package com.rt21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 103;

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (MyApplication) getApplication();
    }

    public void onClickOpenCamera(View view) {
        Intent i = new Intent(getBaseContext(), CameraActivity.class);
        startActivity(i);
    }
}