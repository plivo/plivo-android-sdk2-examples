package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.screens.dial.IncomingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.OngoingCallFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CallsModule {

    @Provides @Singleton
    public OngoingCallFragment ongoingCallFragment() { return OngoingCallFragment.newInstance(); }

    @Provides @Singleton
    public IncomingCallFragment incomingCallFragment() { return IncomingCallFragment.newInstance(); }
}
