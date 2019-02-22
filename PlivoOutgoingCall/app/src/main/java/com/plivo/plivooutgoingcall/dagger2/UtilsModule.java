package com.plivo.plivooutgoingcall.dagger2;

import android.content.Context;

import com.plivo.plivooutgoingcall.utils.AlertUtils;

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
