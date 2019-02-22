package com.plivo.plivoaddressbook.dagger2;

import com.plivo.plivoaddressbook.screens.dial.DialViewModel;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.ContactViewModel;
import com.plivo.plivoaddressbook.screens.login.LoginViewModel;
import com.plivo.plivoaddressbook.services.PlivoBackgroundService;
import com.plivo.plivoaddressbook.services.PlivoFCMService;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        BackendModule.class,
        ReceiverModule.class
})
@Singleton
public interface AppComponent {
    void inject(LoginViewModel loginViewModel);
    void inject(DialViewModel dialViewModel);
    void inject(ContactViewModel contactViewModel);

    void inject(PlivoBackgroundService service);
    void inject(PlivoFCMService service);
}
