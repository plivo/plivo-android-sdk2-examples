package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.screens.dial.DialActivity;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        ViewUtilsModule.class,
        CallsModule.class
}) @Singleton
public interface ViewComponent {
    void inject(LoginActivity activity);
    void inject(DialActivity activity);
}
