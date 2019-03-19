package com.plivo.plivoincomingcall.dagger2;

import android.content.Context;

import com.plivo.plivoincomingcall.screens.dial.DialActivity;
import com.plivo.plivoincomingcall.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.MoreCallsFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.tabs.DialFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (includes = {
        ViewContext.class
})
public class FragmentsModule {

    @Provides @Singleton
    public OngoingCallFragment ongoingCallFragment(@ViewContextQualifier Context context) {
        return OngoingCallFragment.newInstance(((DialActivity)context)::onClickDialer);
    }

    @Provides @Singleton
    public IncomingCallFragment incomingCallFragment(@ViewContextQualifier Context context) {
        return IncomingCallFragment.newInstance();
    }

    @Provides @Singleton
    public MoreCallsFragment moreCallsFragment(@ViewContextQualifier Context context) {
        return (MoreCallsFragment) MoreCallsFragment.newInstance(((DialActivity)context)::onSelectCall)
                .setFragmentLoadedObserver(((DialActivity)context)::onMoreFragmentLoaded);
    }

    @Provides @Singleton
    public DialFragment dialFragment(@ViewContextQualifier Context context) {
        return DialFragment.newInstance();
    }
}
