package com.plivo.plivoincomingcall.screens.login;

import android.app.Application;

import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.BaseViewModel;
import com.plivo.plivoincomingcall.model.User;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.receivers.MyNwkChangeReceiver;
import com.plivo.plivoincomingcall.utils.PreferencesUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends BaseViewModel {

    @Inject
    PlivoBackend backend;

    @Inject
    PreferencesUtils preferencesUtils;

    @Inject
    MyNwkChangeReceiver networkChangeReceiver;

    private MutableLiveData<Boolean> loginSuccessObserver = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Boolean> loginObserver() {
        return loginSuccessObserver;
    }

    void login(String username, String pass, String deviceToken) {
        User logInUser = new User.Builder()
                .setUsername(username)
                .setPassword(pass)
                .setDeviceToken(deviceToken)
                .build();
        login(logInUser);
    }

    void login(User logInUser) {
        getBackgroundTask().submit(() -> {
            if (backend.login(logInUser, success -> postLogin(logInUser, success)) &&
                    getLoggedInUser() != null) {
                postLogin(logInUser, isSameUser(logInUser));
            }
        });
    }

    void reLogin() {
        if (isUserLoggedIn()) {
            login(preferencesUtils.getUser());
        }
    }

    User getLoggedInUser() {
        return preferencesUtils.getUser();
    }

    private boolean isSameUser(User user) {
        if (user == null) return false;

        User availableUser = getLoggedInUser();
        return availableUser != null &&
                user.getUsername().equals(availableUser.getUsername()) &&
                user.getPassword().equals(availableUser.getPassword());
    }

    private void postLogin(User user, boolean success) {
        if (success) {
            preferencesUtils.setLogin(true, user);
        }
        networkChangeReceiver.register(getApplication().getApplicationContext());

        loginSuccessObserver.postValue(success);
    }

    boolean isUserLoggedIn() {
        return preferencesUtils.getUser() != null;
    }

    boolean isLoggedIn() {
        return backend.isLoggedIn();
    }

    boolean isLoginExpired() {
        return preferencesUtils.isLoginExpired();
    }
}
