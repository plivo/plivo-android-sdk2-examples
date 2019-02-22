package com.plivo.plivoregistration;

import android.app.Application;

import com.plivo.plivoregistration.dagger2.AppComponent;
import com.plivo.plivoregistration.dagger2.DaggerAppComponent;

public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setAppComponent(DaggerAppComponent.create());
    }

    private void setAppComponent(AppComponent component) {
        this.appComponent = component;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
