package com.plivo.plivooutgoingcall.screens.dial;

import android.app.Application;
import android.util.Log;

import com.plivo.endpoint.Endpoint.EndpointNotRegisteredException;
import com.plivo.plivooutgoingcall.App;
import com.plivo.plivooutgoingcall.BaseViewModel;
import com.plivo.plivooutgoingcall.plivo.layer.PlivoBackend;
import com.plivo.plivooutgoingcall.plivo.layer.PlivoCallState;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DialViewModel extends BaseViewModel {

    private static final String TAG = DialViewModel.class.getSimpleName();

    @Inject
    PlivoBackend backend;

    private MutableLiveData<Object> logoutSuccess;
    private MutableLiveData<PlivoCallState.OUT_CALL_STATE> callState = new MutableLiveData<>();

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

    LiveData<PlivoCallState.OUT_CALL_STATE> call(String phone_number) {
        getBackgroundTask().submit(() -> {
            try {
                backend.outCall(phone_number, state -> {
                    Log.d(TAG, "current call state: " + state);
                    callState.postValue(state);
                });
            } catch (EndpointNotRegisteredException e) {
                e.printStackTrace();
            }
        });

        return callState;
    }

    LiveData<PlivoCallState.OUT_CALL_STATE> endCall() {
        getBackgroundTask().submit(() -> {
            try {
                backend.hangUp(state -> {
                    Log.d(TAG, "current call state: " + state);
                    callState.postValue(state);
                });
            } catch (EndpointNotRegisteredException e) {
                e.printStackTrace();
            }
        });

        return callState;
    }

    void mute() {
        getBackgroundTask().submit(() -> {
            try {
                backend.mute();
            } catch (EndpointNotRegisteredException e) {
                e.printStackTrace();
            }
        });
    }

    void unmute() {
        getBackgroundTask().submit(() -> {
            try {
                backend.unMute();
            } catch (EndpointNotRegisteredException e) {
                e.printStackTrace();
            }
        });
    }

}
