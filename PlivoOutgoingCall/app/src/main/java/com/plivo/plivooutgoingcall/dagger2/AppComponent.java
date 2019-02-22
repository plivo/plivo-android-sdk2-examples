package com.plivo.plivooutgoingcall.dagger2;

import com.plivo.plivooutgoingcall.screens.dial.DialViewModel;
import com.plivo.plivooutgoingcall.screens.login.LoginViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
    BackendModule.class
})
@Singleton
public interface AppComponent {
    void inject(LoginViewModel loginViewModel);
    void inject(DialViewModel dialViewModel);
}
