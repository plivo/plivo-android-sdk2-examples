package com.plivo.plivoaddressbook.dagger2;

import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;
import com.plivo.plivoaddressbook.layer.plivo.PlivoCallStack;
import com.plivo.plivoaddressbook.layer.impl.PlivoSDKImpl;
import com.plivo.plivoaddressbook.utils.ContactUtils;

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
