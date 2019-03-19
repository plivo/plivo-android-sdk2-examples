package com.plivo.plivoincomingcall;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.plivo.plivoincomingcall.dagger2.AppComponent;
import com.plivo.plivoincomingcall.dagger2.AppContext;
import com.plivo.plivoincomingcall.dagger2.DaggerAppComponent;

public class App extends Application {

    private static final String TAG = Application.class.getSimpleName();
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setAppComponent(DaggerAppComponent.builder().appContext(new AppContext(this)).build());
    }

    private void setAppComponent(AppComponent component) {
        this.appComponent = component;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
