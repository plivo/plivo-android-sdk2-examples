package com.plivo.plivooutgoingcall.dagger2;

import com.plivo.plivooutgoingcall.plivo.layer.PlivoBackend;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BackendModule {

    @Provides @Singleton
    public PlivoBackend plivoBackend() {
        return new PlivoBackend();
    }

}
