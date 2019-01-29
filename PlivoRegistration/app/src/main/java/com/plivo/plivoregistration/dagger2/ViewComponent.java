package com.plivo.plivoregistration.dagger2;

import com.plivo.plivoregistration.screens.dial.DialActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        UtilsModule.class
}) @Singleton
public interface ViewComponent {
    void inject(DialActivity activity);
}
