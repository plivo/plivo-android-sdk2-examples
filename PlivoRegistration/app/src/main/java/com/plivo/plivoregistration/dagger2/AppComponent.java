package com.plivo.plivoregistration.dagger2;


import com.plivo.plivoregistration.screens.dial.DialViewModel;
import com.plivo.plivoregistration.screens.login.LoginViewModel;

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
