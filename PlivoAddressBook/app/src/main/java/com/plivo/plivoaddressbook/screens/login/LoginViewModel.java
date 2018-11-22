package com.plivo.plivoaddressbook.screens.login;

import android.app.Application;

import com.plivo.plivoaddressbook.App;
import com.plivo.plivoaddressbook.BaseViewModel;
import com.plivo.plivoaddressbook.model.User;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;
import com.plivo.plivoaddressbook.utils.PreferencesUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends BaseViewModel {

    @Inject
    PlivoBackend backend;

    @Inject
    PreferencesUtils preferencesUtils;

    private MutableLiveData<Boolean> loginSuccessObserver = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Boolean> loginObserver() {
        return loginSuccessObserver;
    }

    void login(String username, String pass) {
        User logInUser = new User.Builder()
                .setUsername(username)
                .setPassword(pass)
                .build();

        getBackgroundTask().submit(() -> {
            backend.login(logInUser, success -> {
                if (success) preferencesUtils.setLogin(true, logInUser);
                loginSuccessObserver.postValue(success);
            });
        });
    }

    boolean isLoggedIn() {
        if (isLoginExpired()) {
            backend.keepAlive(success -> loginSuccessObserver.postValue(success));
        }
        return backend.isLoggedIn() && !isLoginExpired();
    }

    boolean isLoginExpired() {
        return preferencesUtils.isLoginExpired();
    }
}
