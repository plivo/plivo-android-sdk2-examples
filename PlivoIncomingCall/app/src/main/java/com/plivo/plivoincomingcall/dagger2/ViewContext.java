package com.plivo.plivoincomingcall.dagger2;

import android.app.Activity;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewContext {

    private Context context;

    public ViewContext(Activity context) {
        this.context = context;
    }

    @Provides @Singleton @ViewContextQualifier
    public Context getContext() {
        return context;
    }

}
