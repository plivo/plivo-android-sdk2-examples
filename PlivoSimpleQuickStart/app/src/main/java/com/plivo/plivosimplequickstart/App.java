package com.plivo.plivosimplequickstart;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private PlivoBackEnd backend;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.options.put("context",getApplicationContext());
        backend = PlivoBackEnd.newInstance();
        backend.init(BuildConfig.DEBUG);
    }

    public PlivoBackEnd backend() {
        return backend;
    }
}
