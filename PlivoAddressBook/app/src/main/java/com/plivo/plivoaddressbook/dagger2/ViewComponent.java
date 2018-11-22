package com.plivo.plivoaddressbook.dagger2;

import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.calls.MoreCallsFragment;
import com.plivo.plivoaddressbook.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.CallLogFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.ContactsFragment;
import com.plivo.plivoaddressbook.screens.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AdapterModule.class
}) @Singleton
public interface ViewComponent {
    void inject(LoginActivity activity);
    void inject(DialActivity activity);

    void inject(MoreCallsFragment fragment);
    void inject(CallLogFragment fragment);
    void inject(ContactsFragment fragment);
    void inject(OngoingCallFragment fragment);
}
