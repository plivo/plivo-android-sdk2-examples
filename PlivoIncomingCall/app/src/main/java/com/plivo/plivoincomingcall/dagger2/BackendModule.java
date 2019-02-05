package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.layer.plivo.PlivoCall;
import com.plivo.plivoincomingcall.layer.impl.PlivoSDKImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BackendModule {

    @Provides @Singleton
    public PlivoBackend plivoBackend(PlivoCall callObj) {
        return new PlivoSDKImpl(callObj);
    }

    @Provides @Singleton
    public PlivoCall plivoCall() {
        return new PlivoCall();
    }

}
