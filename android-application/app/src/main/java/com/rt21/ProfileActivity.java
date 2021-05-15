package com.rt21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtUsername;
    private TextView txtAge;
    private TextView txtEmail;

    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        app = (MyApplication)getApplication();

        txtName = findViewById(R.id.txtFullname);
        txtUsername = findViewById(R.id.txtUsername);
        txtAge = findViewById(R.id.txtAge);
        txtEmail = findViewById(R.id.txtEmail);

        txtName.setText(String.format(getString(R.string.name_s_profile), app.user.getName()));
        txtUsername.setText(String.format(getString(R.string.username_s_profile), app.user.getUsername()));
        txtEmail.setText(String.format(getString(R.string.email_s_profile), app.user.getEmail()));
        txtAge.setText(String.format(getString(R.string.age_s_profile), Integer.toString(app.user.getAge())));
    }
}