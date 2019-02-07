package com.plivo.plivoaddressbook;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.plivo.plivoaddressbook.dagger2.AppComponent;
import com.plivo.plivoaddressbook.dagger2.AppContext;
import com.plivo.plivoaddressbook.dagger2.DaggerAppComponent;
import com.plivo.plivoaddressbook.services.PlivoBackgroundService;

public class App extends Application {

    private static final String TAG = Application.class.getSimpleName();
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setAppComponent(DaggerAppComponent.builder().appContext(new AppContext(this)).build());
        stopBakgroundService();
    }

    public void startBakgroundService() {
//        Log.d(TAG, "startBackgroundService");
//        Intent intent = new Intent(this, PlivoBackgroundService.class)
//                .putExtra(PlivoBackgroundService.COMMAND, PlivoBackgroundService.START);
//        startedService(intent);
    }

    public void stopBakgroundService() {
//        Log.d(TAG, "stopBackgroundService");
//        Intent intent = new Intent(this, PlivoBackgroundService.class)
//                .putExtra(PlivoBackgroundService.COMMAND, PlivoBackgroundService.STOP);
//        startedService(intent);
    }

    private void startedService(Intent serviceIntent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(serviceIntent);
        } else {
            startForegroundService(serviceIntent);
        }
    }

    private void setAppComponent(AppComponent component) {
        this.appComponent = component;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
