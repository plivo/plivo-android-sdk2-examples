package com.plivo.plivoaddressbook.screens.dial.tabs.contacts;

import android.app.Application;

import com.plivo.plivoaddressbook.App;
import com.plivo.plivoaddressbook.BaseViewModel;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.Contact;
import com.plivo.plivoaddressbook.utils.ContactUtils;
import com.plivo.plivoaddressbook.utils.PreferencesUtils;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ContactViewModel extends BaseViewModel {
    private static final String TAG = ContactViewModel.class.getSimpleName();

    @Inject
    PlivoBackend backend;

    @Inject
    PreferencesUtils preferencesUtils;

    @Inject
    ContactUtils contactUtils;

    private MutableLiveData<List<Call>> callLogObserver = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsObserver = new MutableLiveData<>();

    public ContactViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getAppComponent().inject(this);
    }

    LiveData<List<Call>> callLogObserver() { return callLogObserver; }

    LiveData<List<Contact>> contactsObserver() { return contactsObserver; }

    void getCallLog() {
        getBackgroundTask().submit(() -> {
            callLogObserver.postValue(contactUtils.getCallLog());
        });
    }

    void getContactsList() {
        getBackgroundTask().submit(() -> {
           contactsObserver.postValue(contactUtils.getAllContacts());
        });
    }

}
