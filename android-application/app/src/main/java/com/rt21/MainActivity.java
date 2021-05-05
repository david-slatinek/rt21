package com.rt21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final  String TAG = MainActivity.class.getSimpleName();
    private MyApplication app;

    public static final int ACTIVITY_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.print("Hello");

        // leads to error:
//        Caused by: java.lang.ClassCastException: android.app.Application cannot be cast to com.rt21.MyApplication
//        at com.rt21.MainActivity.onCreate(MainActivity.java:25)
        // app = (MyApplication) getApplication();
    }

    public void onClickOpenLogin(View view) {
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        i.putExtra(LoginActivity.FORM_MODE_ID, LoginActivity.FORM_MODE_LOGIN);
        startActivityForResult(i, LoginActivity.ACTIVITY_ID);
    }

    public void onClickOpenRegistration(View view) {
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        i.putExtra(LoginActivity.FORM_MODE_ID, LoginActivity.FORM_MODE_REGISTER);
        startActivityForResult(i, LoginActivity.ACTIVITY_ID);
    }

    public void onClickOpenInfo(View view) {
        Intent i = new Intent(getBaseContext(), InfoActivity.class);
        startActivity(i);
    }

    public void onClickOpenCamera(View view) {
        Intent i = new Intent(getBaseContext(), CameraActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.ACTIVITY_ID) {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getIntExtra(LoginActivity.RESULT_VAL, -1) == 0) {
                    Timber.i("Received: %s which is Register code.", data.getExtras().get(LoginActivity.RESULT_VAL));
                }
                else if (data.getIntExtra(LoginActivity.RESULT_VAL, -1) == 1) {
                    Timber.i("Received: %s which is Login code.", data.getExtras().get(LoginActivity.RESULT_VAL));
                }

                //TODO - open activity containing user profile, how many km he drove and how many road signs he passed
            }
        }
        // needs implementation
//        if (requestCode == ShowLocations.ACTIVITY_ID) {
//            this.finishAffinity();
//        }
    }
}