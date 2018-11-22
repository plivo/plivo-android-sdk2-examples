package com.plivo.plivoaddressbook.dagger2;

import android.content.Context;

import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoaddressbook.screens.dial.calls.MoreCallsFragment;
import com.plivo.plivoaddressbook.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.DialFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.CallLogFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.ContactsFragment;

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
    public CallLogFragment callLogFragment(@ViewContextQualifier Context context) {
        return CallLogFragment.newInstance(((DialActivity)context)::onClickCallLog);
    }

    @Provides @Singleton
    public ContactsFragment contactsFragment(@ViewContextQualifier Context context) {
        return ContactsFragment.newInstance(((DialActivity)context)::onClickContact);
    }

    @Provides @Singleton
    public DialFragment dialFragment(@ViewContextQualifier Context context) {
        return DialFragment.newInstance();
    }
}
