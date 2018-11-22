package com.plivo.plivoaddressbook.dagger2;

import android.content.Context;

import com.plivo.plivoaddressbook.utils.AlarmUtils;
import com.plivo.plivoaddressbook.utils.AlertUtils;
import com.plivo.plivoaddressbook.utils.DateUtils;
import com.plivo.plivoaddressbook.utils.TickManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (includes = {
        ViewContext.class
})
public class ViewUtilsModule {

    @Provides @Singleton
    public AlertUtils alertUtils(@ViewContextQualifier Context context) {
        return new AlertUtils(context);
    }

    @Provides @Singleton
    public AlarmUtils alarmtUtils(@ViewContextQualifier Context context) {
        return new AlarmUtils(context);
    }

    @Provides @Singleton
    public DateUtils dateUtils(@ViewContextQualifier Context context) {
        return new DateUtils(context);
    }

    @Provides @Singleton
    public TickManager tickManager() {
        return new TickManager();
    }
}
