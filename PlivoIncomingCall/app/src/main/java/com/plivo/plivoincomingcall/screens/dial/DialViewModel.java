package com.plivo.plivoincomingcall.screens.dial;

import android.app.Application;
import android.util.Log;

import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.BaseViewModel;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.model.User;
import com.plivo.plivoincomingcall.utils.PreferencesUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DialViewModel extends BaseViewModel {

    private static final String TAG = DialViewModel.class.getSimpleName();

    @Inject
    PlivoBackend backend;

    @Inject
    PreferencesUtils preferencesUtils;

    private MutableLiveData<Object> logoutSuccessObserver = new MutableLiveData<>();

    private MutableLiveData<Call> incomingCallObserver = new MutableLiveData<>();

    public DialViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Object> logoutObserver() {
        return logoutSuccessObserver;
    }

    void logout() {
        if (!isLoggedIn()) {
            preferencesUtils.setLogin(false);
            logoutSuccessObserver.postValue(null);
        } else {
            getBackgroundTask().submit(() -> {
                backend.logout(() -> {
                    preferencesUtils.setLogin(false);
                    logoutSuccessObserver.postValue(null);
                });
            });
        }
    }

    LiveData<Call> incomingCallObserver() {
        backend.setIncomingCallListener(call -> incomingCallObserver.postValue(call));
        return incomingCallObserver;
    }

    LiveData<String> incomingDTMFObserver() {
        MutableLiveData<String> dtmf = new MutableLiveData<>();
        backend.setIncomingDTMFListener(digit -> dtmf.postValue(digit));
        return dtmf;
    }

    void hangup() {
        getBackgroundTask().submit(() -> backend.hangUp());
    }

    void reject() {
        getBackgroundTask().submit(() -> backend.reject());
    }

    void answer() {
        getBackgroundTask().submit(() -> backend.answer());
    }

    void mute() {
        getBackgroundTask().submit(() -> backend.mute());
    }

    void unmute() {
        getBackgroundTask().submit(() -> backend.unMute());
    }

    void sendDTMF(String digit) {
        getBackgroundTask().submit(() -> backend.sendDigit(digit));
    }

    boolean isLoggedIn() {
        return backend.isLoggedIn();
    }

    User getLoggedInUser() {
        return preferencesUtils.getUser();
    }

    Call getCurrentCall() { return backend.getCurrentCall(); }

    void triggerIncomingCall() {
        Log.d(TAG, "triggerIncomingCall");
        incomingCallObserver.postValue(backend.getCurrentCall());
    }

}
