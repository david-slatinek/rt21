package com.rt21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import timber.log.Timber;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity {
    public static final int ACTIVITY_ID = 101;

    public static final String FORM_MODE_ID = "FORM_MODE";
    public static final int FORM_MODE_LOGIN = 0;
    public static final int FORM_MODE_REGISTER = 1;

    public static final String RESULT_VAL = "RESULT";

    private int activityMode;
    private MyApplication app;

    private TextView txtTitle;
    private EditText etName;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (MyApplication) getApplication();

        txtTitle = findViewById(R.id.txtLoginRegisterTitle);
        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin_Register = findViewById(R.id.login_registerBtn);

        activityMode = getIntent().getExtras().getInt(FORM_MODE_ID);
        Timber.i("Opening as: %s", activityMode);
        updateGUI();
    }

    private void updateGUI() {
        if (activityMode == FORM_MODE_LOGIN) {
            txtTitle.setText(getString(R.string.login_page_title));
            etName.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
            btnLogin_Register.setText(getString(R.string.login_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etUsername.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {
                        //TODO - check if user exists
                        //if (login.success) {
                        Timber.i("Login pressed");
                        returnBack(1);
                        //} else {
                        //    CommonMethods.displayToastShort(getApplicationContext(), "User doesn't exist!");
                        //}
                    } else {
                        CommonMethods.displayToastLong("No login data provided!", getApplicationContext());
                    }
                }
            });
        } else if (activityMode == FORM_MODE_REGISTER) {
            txtTitle.setText(getString(R.string.register_page_title));
            btnLogin_Register.setText(getString(R.string.register_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etName.getText().toString().equals("") && !etUsername.getText().toString().equals("") &&
                            !etEmail.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {
                        //TODO - check if user exists and if data is compatible for registration
                        //if (registration.success) {
                        Timber.i("Register pressed");
                        returnBack(0);
                        //} else {
                        //    CommonMethods.displayToastShort(getApplicationContext(), "Registration failed!");
                        //}
                    } else {
                        CommonMethods.displayToastShort("No registration data provided!", getApplicationContext());
                    }
                }
            });
        }
    }

    //val = 0 -> register, val = 1 -> login, val = -1 no action
    public void returnBack(int val) {
        if (val != -1) {
            Intent i = getIntent();
            i.putExtra(RESULT_VAL, val);
            setResult(RESULT_OK, i);
        }
        finish();
    }
}