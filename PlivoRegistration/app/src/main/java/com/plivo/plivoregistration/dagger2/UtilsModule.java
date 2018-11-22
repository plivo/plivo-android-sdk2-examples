package com.plivo.plivoregistration.dagger2;

import android.content.Context;


import com.plivo.plivoregistration.utils.AlertUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (includes = {
        ViewContext.class
})
public class UtilsModule {

    @Provides @Singleton
    public AlertUtils alertUtils(Context viewContext) {
        return new AlertUtils(viewContext);
    }
}
