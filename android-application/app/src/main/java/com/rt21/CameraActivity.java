package com.rt21;

// activity to test Camera class

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
}