package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.layer.plivo.PlivoCallStack;
import com.plivo.plivoincomingcall.layer.impl.PlivoSDKImpl;
import com.plivo.plivoincomingcall.utils.ContactUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (includes = {
        UtilsModule.class
})
public class BackendModule {

    @Provides @Singleton
    public PlivoBackend plivoBackend(PlivoCallStack callstackObj, ContactUtils contactUtils) {
        return new PlivoSDKImpl(callstackObj, contactUtils);
    }

    @Provides @Singleton
    public PlivoCallStack plivoCall() {
        return new PlivoCallStack();
    }

}
