package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.receivers.MyNwkChangeReceiver;

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
