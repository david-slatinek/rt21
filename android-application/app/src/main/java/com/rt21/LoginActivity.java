package com.minty.treasurehunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.charset.IllegalCharsetNameException;

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
    private TextView txtName;
    private EditText etName;
    private TextView txtUsername;
    private EditText etUsername;
    private TextView txtEmail;
    private EditText etEmail;
    private TextView txtPassword;
    private EditText etPassword;
    private Button btnLogin_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (MyApplication) getApplication();

        txtTitle = findViewById(R.id.txtLoginRegisterTitle);
        txtName = findViewById(R.id.txtName);
        etName = findViewById(R.id.etName);
        txtUsername = findViewById(R.id.txtUsername);
        etUsername = findViewById(R.id.etUsername);
        txtEmail = findViewById(R.id.txtEmail);
        etEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.txtPassword);
        etPassword = findViewById(R.id.etPassword);
        btnLogin_Register = findViewById(R.id.login_registerBtn);

        activityMode = getIntent().getExtras().getInt(FORM_MODE_ID);
        Timber.i("Opening as: %s", activityMode);
        updateGUI();
    }

    private void updateGUI() {
        if (activityMode == FORM_MODE_LOGIN) {
            txtTitle.setText(getString(R.string.login_page_title));
            txtName.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
            btnLogin_Register.setText(getString(R.string.login_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.i("Login pressed");
                    returnBack(1);
                }
            });
        } else if (activityMode == FORM_MODE_REGISTER) {
            txtTitle.setText(getString(R.string.register_page_title));
            btnLogin_Register.setText(getString(R.string.register_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.i("Register pressed");
                    returnBack(0);
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

    public void onClickGoBackFromLogin(View view) {
        returnBack(-1);
    }
}