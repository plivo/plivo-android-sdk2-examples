package com.plivo.plivoaddressbook.screens.dial;

import android.app.Application;
import android.util.Log;

import com.plivo.plivoaddressbook.App;
import com.plivo.plivoaddressbook.BaseViewModel;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.User;
import com.plivo.plivoaddressbook.receivers.MyNwkChangeReceiver;
import com.plivo.plivoaddressbook.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Inject
    MyNwkChangeReceiver networkChangeReceiver;

    private MutableLiveData<Boolean> logoutSuccessObserver = new MutableLiveData<>();

    private MutableLiveData<Call> callStackObserver = new MutableLiveData<>();

    public DialViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<Boolean> logoutObserver() {
        return logoutSuccessObserver;
    }

    void logout() {
        getBackgroundTask().submit(() -> {
            if (!backend.logout(() -> postLogout(true))) {
                postLogout(false);
            }
        });
    }

    private void postLogout(boolean success) {
        if (success) {
            preferencesUtils.setLogin(false);
            backend.clearCallStack();
            networkChangeReceiver.unregister(getApplication().getApplicationContext());
        }
        logoutSuccessObserver.postValue(success);
    }

    LiveData<Call> callStackObserver() {
        backend.setCallStackListener(call -> callStackObserver.postValue(call));
        return callStackObserver;
    }

    public LiveData<String> incomingDTMFObserver() {
        MutableLiveData<String> dtmf = new MutableLiveData<>();
        backend.setIncomingDTMFListener(digit -> dtmf.postValue(digit));
        return dtmf;
    }

    public void call(Call to) {
        if (to == null || to.getContact() == null) return;

        getBackgroundTask().submit(() -> {
            backend.outCall(to.getContact().getPhoneNumber());
        });
    }

    public void terminate() {
        getBackgroundTask().submit(() -> backend.terminateCall());
    }
    public void hangup() {
        getBackgroundTask().submit(() -> backend.hangUp());
    }

    public void reject() {
        getBackgroundTask().submit(() -> backend.reject());
    }

    public void answer() {
        getBackgroundTask().submit(() -> backend.answer());
    }

    public void mute() {
        getBackgroundTask().submit(() -> backend.mute());
    }

    public void unmute() {
        getBackgroundTask().submit(() -> backend.unMute());
    }

    public void hold() {
        getBackgroundTask().submit(() -> backend.hold());
    }

    public void unHold() {
        getBackgroundTask().submit(() -> backend.unHold());
    }

    public void sendDTMF(String digit) {
        getBackgroundTask().submit(() -> backend.sendDigit(digit));
    }

    boolean isLoggedIn() {
        return backend.isLoggedIn();
    }

    public User getLoggedInUser() {
        return preferencesUtils.getUser();
    }

    boolean isUserLoggedIn() {
        return getLoggedInUser() != null;
    }

    public Call getCurrentCall() { return backend.getCurrentCall(); }

    public List<Call> getAvailableCalls() {
        return backend.getAvailableCalls();
    }

    public List<Call> getOtherCalls() {
        ArrayList otherCalls = new ArrayList();

        if (getCurrentCall() != null) {
            for (Call c : getAvailableCalls()) {
                if (c.getId().equalsIgnoreCase(getCurrentCall().getId())) {
                    continue;
                }
                otherCalls.add(c);
            }
        }
        return otherCalls;
    }

    public void setCall(Call call) {
        backend.setCurrentCall(call);
    }

    public boolean isCarrierCallInProgress() {
        return preferencesUtils.isCarrierCallInProgress();
    }

    public void setCarrierCallInProgress(boolean inProgress) {
        preferencesUtils.setIsCarrierCallInProgress(inProgress);
    }

    void triggerStackChange() {
        callStackObserver.postValue(backend.getCurrentCall());
    }

}
