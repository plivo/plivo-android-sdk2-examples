package com.plivo.plivooutgoingcall.dagger2;

import com.plivo.plivooutgoingcall.screens.dial.DialActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        UtilsModule.class
}) @Singleton
public interface ViewComponent {
    void inject(DialActivity activity);
}
