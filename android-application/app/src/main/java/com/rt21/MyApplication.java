package com.rt21;

import android.app.Application;
import android.graphics.RadialGradient;

import com.rt21.data.User;

import java.util.Random;

import timber.log.Timber;

public class MyApplication extends Application {
    private final String keyName = "X-API-Key";
    private final String apiKey = "04fca805-c486-4519-9bdb-7dd80733dfd1";

    public String driveID;
    public User user;
    public User testUser;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.tag("My application");
        Timber.d("Application created");

        Random rand = new Random();

        testUser = new User("testId123151assd","Test Subject", "Test Username", "test.subject@mail.com", 21);
    }

    public String getKeyName() {
        return keyName;
    }

    public String getApiKey() {
        return apiKey;
    }
}
