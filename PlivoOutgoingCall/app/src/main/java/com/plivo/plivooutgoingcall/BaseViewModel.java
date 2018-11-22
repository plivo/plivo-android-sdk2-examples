package com.plivo.plivooutgoingcall;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class BaseViewModel extends AndroidViewModel {
    private ExecutorService backgroundTask;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    protected ExecutorService getBackgroundTask() {
        if (backgroundTask == null) {
            backgroundTask = Executors.newCachedThreadPool();
        }
        return backgroundTask;
    }
}
