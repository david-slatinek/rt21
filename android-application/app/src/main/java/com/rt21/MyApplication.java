package com.rt21;

import android.app.Application;
import android.graphics.RadialGradient;

import com.rt21.data.User;

import java.util.Random;

import timber.log.Timber;

public class MyApplication extends Application {

    public User testUser;
    public int distance;
    public int signs;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.tag("My application");
        Timber.d("Application created");

        Random rand = new Random();

        testUser = new User("Test Subject", "Test Username", "test.subject@mail.com", "testPswd");
        distance = rand.nextInt(1000);
        signs = rand.nextInt(1000);
    }
}
