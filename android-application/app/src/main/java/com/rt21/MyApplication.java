package com.minty.treasurehunt;

import android.app.Application;

import com.minty.data.MyLocation;
import com.minty.data.User;

import timber.log.Timber;

public class MyApplication extends Application {

    public MyLocation location;
    public User testingUser;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.tag("My application");
        Timber.d("Application created");

        testingUser = new User("Test", "UsernameTest", "test@mail.com", "testPasswd");
    }
}
