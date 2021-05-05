package com.minty.treasurehunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class InfoActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    public void onClickGoBackFromInfoActivity(View view) {
        finish();
    }
}