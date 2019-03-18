package com.plivo.plivoincomingcall.dagger2;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.plivo.plivoincomingcall.BaseActivity;
import com.plivo.plivoincomingcall.adapters.MoreCallsAdapter;
import com.plivo.plivoincomingcall.screens.dial.tabs.DialFragment;
import com.plivo.plivoincomingcall.screens.dial.tabs.TabFragment;
import com.plivo.plivoincomingcall.utils.DateUtils;
import com.plivo.plivoincomingcall.utils.TickManager;

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
}
