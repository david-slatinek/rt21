package com.rt21;

import android.app.Application;

import com.rt21.data.User;

import timber.log.Timber;

public class MyApplication extends Application {

    User testUser;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.tag("My application");
        Timber.d("Application created");

        testUser = new User("Test Subject", "Test Username", "test.subject@mail.com", "testPswd");
    }
}
