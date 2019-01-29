package com.plivo.plivoregistration.dagger2;

import android.app.Activity;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewContext {

    private Context context;

    public ViewContext(Activity context) {
        this.context = context;
    }

    @Provides @Singleton
    public Context getContext() {
        return context;
    }
}
