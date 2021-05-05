package com.rt21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 100;
    private static final  String TAG = MainActivity.class.getSimpleName();
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.print("Hello");

        app = (MyApplication) getApplication();
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

                Intent i = new Intent(getBaseContext(), HomeActivity.class);
                startActivityForResult(i, HomeActivity.ACTIVITY_ID);
            }
        }
        if (requestCode == HomeActivity.ACTIVITY_ID) {
            this.finishAffinity();
        }
    }
}