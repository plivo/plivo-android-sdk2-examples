package com.plivo.plivooutgoingcall.screens.login;

import android.app.Application;

import com.plivo.plivooutgoingcall.App;
import com.plivo.plivooutgoingcall.BaseViewModel;
import com.plivo.plivooutgoingcall.model.User;
import com.plivo.plivooutgoingcall.plivo.layer.PlivoBackend;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends BaseViewModel {

    @Inject
    PlivoBackend backend;

    private MutableLiveData<Boolean> loginSuccess;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Boolean> login(String username, String pass) {
        loginSuccess = new MutableLiveData<>();
        User logInUser = new User.Builder()
                .setUsername(username)
                .setPassword(pass)
                .build();

        getBackgroundTask().submit(() -> {
            backend.login(logInUser, success -> {
                loginSuccess.postValue(success);
            });
        });

        return loginSuccess;
    }
}
