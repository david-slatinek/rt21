package com.rt21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rt21.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

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
    private EditText etAge;
    private Button btnLogin_Register;
    private CheckBox chkStayLoggedIn;

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
        etAge = findViewById(R.id.etAge);
        btnLogin_Register = findViewById(R.id.login_registerBtn);
        chkStayLoggedIn = findViewById(R.id.chk_StayLoggedIn);

        activityMode = getIntent().getExtras().getInt(FORM_MODE_ID);
        Timber.i("Opening as: %s", activityMode);
        updateGUI();
    }

    private void updateGUI() {
        if (activityMode == FORM_MODE_LOGIN) {
            txtTitle.setText(getString(R.string.login_page_title));
            etName.setVisibility(View.GONE);
            etUsername.setVisibility(View.GONE);
            etAge.setVisibility(View.GONE);
            btnLogin_Register.setText(getString(R.string.login_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etEmail.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {
                        boolean success = onLogin();
                        if (success) {
                            if (chkStayLoggedIn.isChecked()) {
                                SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = sprefs.edit();
                                editor.putBoolean("remember_user", true);
                                editor.putString("_id", app.user.getId());
                                editor.putString("username", app.user.getUsername());
                                editor.putString("email", app.user.getEmail());
                                editor.putInt("age", app.user.getAge());
                                editor.putString("fullname", app.user.getName());
                                editor.apply();
                            }
                            returnBack(1);
                        } else {
                            CommonMethods.displayToastShort("User doesn't exist!", getApplicationContext());
                        }
                    } else {
                        CommonMethods.displayToastLong("No login data provided!", getApplicationContext());
                    }
                }
            });
        } else if (activityMode == FORM_MODE_REGISTER) {
            txtTitle.setText(getString(R.string.register_page_title));
            chkStayLoggedIn.setVisibility(View.GONE);
            btnLogin_Register.setText(getString(R.string.register_button));
            btnLogin_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etName.getText().toString().equals("") && !etUsername.getText().toString().equals("") &&
                            !etEmail.getText().toString().equals("") && !etPassword.getText().toString().equals("")) {
                        boolean success = onRegistration();
                        if (success) {
                            Timber.i("Register pressed");
                            returnBack(0);
                        } else {
                            CommonMethods.displayToastShort("Registration failed!", getApplicationContext());
                        }
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

    private boolean onLogin() {
        /**
         * Login request to get data of a testUser from API
         * Ion sends POST request to api with id in url and header that contains api key name and api key value
         * as a result it gets JsonObject with user data
         */
        try {
            JsonObject json = Ion.with(getBaseContext())
                    .load("POST","https://rt21-api.herokuapp.com/api/user/login")
                    .setHeader(app.getKeyName(), app.getApiKey())
                    .setBodyParameter("email", etEmail.getText().toString())
                    .setBodyParameter("password", etPassword.getText().toString())
                    .asJsonObject()
                    .get();

            JSONObject jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("error")) {
                Toast.makeText(getBaseContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(getBaseContext(), "Log in success", Toast.LENGTH_SHORT).show();
                String name = jsonObject.getString("name");
                String last_name = jsonObject.getString("last_name");
                String nickname = jsonObject.getString("nickname");
                String email = jsonObject.getString("email");
                int age = Integer.parseInt(jsonObject.getString("age"));
                JSONObject json_id = jsonObject.getJSONObject("_id");
                String _id = json_id.getString("$oid");

                app.user = new User(_id, name+" "+last_name, nickname, email, age);
                Timber.i(app.user.toString());
                return true;
            }


        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO - i works but Callback doesn't start
    private boolean onRegistration() {
        Ion.with(getBaseContext())
                .load("POST","https://rt21-api.herokuapp.com/api/user/login")
                .setHeader(app.getKeyName(), app.getApiKey())
                .setMultipartParameter("name", etName.getText().toString().split("\\s+")[0])
                .setMultipartParameter("last_name", etName.getText().toString().split("\\s+")[1])
                .setMultipartParameter("age", etAge.getText().toString())
                .setMultipartParameter("age", "20")
                .setMultipartParameter("nickname", etUsername.getText().toString())
                .setMultipartParameter("email", etEmail.getText().toString())
                .setMultipartParameter("password", etPassword.getText().toString())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                   public void onCompleted(Exception e, JsonObject result) {
                       Timber.i(result.toString());
                   }
                });

        return true;
    }
}