package com.plivo.plivosimplequickstart;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private PlivoBackEnd backend;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        backend = PlivoBackEnd.newInstance(context);
        backend.init(BuildConfig.DEBUG);
    }

    public PlivoBackEnd backend() {
        return backend;
    }
}
