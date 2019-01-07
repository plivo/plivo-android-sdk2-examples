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

        getBackgroundTask().submit(() -> backend.login(logInUser, success -> postLogin(logInUser, success)));
    }

    private void postLogin(User user, boolean success) {
        if (success) {
            preferencesUtils.setLogin(true, user);
        }
        loginSuccessObserver.postValue(success);
    }

    void registerFCMToken(String token) {
        getBackgroundTask().submit(() -> backend.registerFCMToken(token));
    }

    boolean isUserLoggedIn() {
        return preferencesUtils.getUser() != null;
    }

    boolean isLoginExpired() {
        return preferencesUtils.isLoginExpired();
    }
}
