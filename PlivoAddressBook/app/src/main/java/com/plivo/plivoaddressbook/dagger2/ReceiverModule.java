package com.plivo.plivoaddressbook.dagger2;

import com.plivo.plivoaddressbook.receivers.MyNwkChangeReceiver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ReceiverModule {

    @Provides @Singleton
    public MyNwkChangeReceiver networkChangeReceiver() {
        return new MyNwkChangeReceiver();
    }
}
