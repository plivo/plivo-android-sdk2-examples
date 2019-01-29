package com.plivo.plivoregistration.screens.dial;

import android.app.Application;

import com.plivo.plivoregistration.App;
import com.plivo.plivoregistration.BaseViewModel;
import com.plivo.plivoregistration.plivo.layer.PlivoBackend;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DialViewModel extends BaseViewModel {

    @Inject
    PlivoBackend backend;

    private MutableLiveData<Object> logoutSuccess;

    public DialViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Object> logout() {
        logoutSuccess = new MutableLiveData<>();

        getBackgroundTask().submit(() -> {
            backend.logout(() -> {
                logoutSuccess.postValue(null);
            });
        });
        return logoutSuccess;
    }
}
