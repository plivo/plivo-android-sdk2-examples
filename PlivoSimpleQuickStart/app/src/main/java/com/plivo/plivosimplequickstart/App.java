package com.plivo.plivosimplequickstart;

import android.app.Application;

public class App extends Application {

    private PlivoBackEnd backend;

    @Override
    public void onCreate() {
        super.onCreate();
        backend = PlivoBackEnd.newInstance();
        backend.init(BuildConfig.DEBUG);
    }

    public PlivoBackEnd backend() {
        return backend;
    }
}
