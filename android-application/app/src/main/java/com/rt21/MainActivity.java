package com.rt21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

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

        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sprefs.getBoolean("remember_user", false)) {

            String _id = sprefs.getString("_id", "");
            String username = sprefs.getString("username", "");
            String fullname = sprefs.getString("fullname", "");
            String email = sprefs.getString("email", "");
            int age = sprefs.getInt("age", 0);

            if (!_id.equals("") && !username.equals("") && !fullname.equals("") &&
                !email.equals("") && age != 0)
            {
                Intent i = new Intent(getBaseContext(), HomeActivity.class);
                startActivityForResult(i, HomeActivity.ACTIVITY_ID);
            } else {
                Timber.i("Error getting shared preference data.");
            }
        }
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
                //Register return
                if (data.getIntExtra(LoginActivity.RESULT_VAL, -1) == 0) {
                    Timber.i("Received: %s which is Register code.", data.getExtras().get(LoginActivity.RESULT_VAL));

                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    i.putExtra(LoginActivity.FORM_MODE_ID, LoginActivity.FORM_MODE_LOGIN);
                    startActivityForResult(i, LoginActivity.ACTIVITY_ID);

                }
                //Login return
                else if (data.getIntExtra(LoginActivity.RESULT_VAL, -1) == 1) {
                    Timber.i("Received: %s which is Login code.", data.getExtras().get(LoginActivity.RESULT_VAL));

                    Intent i = new Intent(getBaseContext(), HomeActivity.class);
                    startActivityForResult(i, HomeActivity.ACTIVITY_ID);
                }
            }
        }
        if (requestCode == HomeActivity.ACTIVITY_ID) {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getBooleanExtra("sign_out", false)) {
                    Toast.makeText(getBaseContext(), "Log out", Toast.LENGTH_SHORT).show();
                }
            } else {
               this.finishAffinity();
            }
        }
    }
}