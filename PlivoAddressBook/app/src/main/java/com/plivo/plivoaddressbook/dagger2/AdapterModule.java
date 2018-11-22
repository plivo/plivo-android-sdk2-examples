package com.plivo.plivoaddressbook.dagger2;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.plivo.plivoaddressbook.BaseActivity;
import com.plivo.plivoaddressbook.adapters.CallLogAdapter;
import com.plivo.plivoaddressbook.adapters.ContactsAdapter;
import com.plivo.plivoaddressbook.adapters.MoreCallsAdapter;
import com.plivo.plivoaddressbook.adapters.TabsPagerAdapter;
import com.plivo.plivoaddressbook.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.DialFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.CallLogFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.contacts.ContactsFragment;
import com.plivo.plivoaddressbook.screens.dial.tabs.TabFragment;
import com.plivo.plivoaddressbook.utils.DateUtils;
import com.plivo.plivoaddressbook.utils.TickManager;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        ViewContext.class,
        ViewUtilsModule.class,
        FragmentsModule.class
})
public class AdapterModule {

    @Provides @Singleton
    MoreCallsAdapter moreListAdapter(@ViewContextQualifier Context context, DateUtils dateUtils, TickManager tickManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new MoreCallsAdapter(new ArrayList<>(), dateUtils, displayMetrics, tickManager);
    }

    @Provides @Singleton
    TabsPagerAdapter tabsPagerAdapter(@ViewContextQualifier Context context,
                                      CallLogFragment callLogFragment,
                                      DialFragment dialFragment,
                                      ContactsFragment contactsFragment) {
        return new TabsPagerAdapter(((BaseActivity)context).getSupportFragmentManager(),
                new TabFragment[] {callLogFragment, dialFragment, contactsFragment});
    }

    @Provides @Singleton
    CallLogAdapter callLogAdapter(DateUtils dateUtils) {
        return new CallLogAdapter(new ArrayList<>(), dateUtils);
    }

    @Provides @Singleton
    ContactsAdapter contactsAdapter() {
        return new ContactsAdapter(new ArrayList<>());
    }

}
