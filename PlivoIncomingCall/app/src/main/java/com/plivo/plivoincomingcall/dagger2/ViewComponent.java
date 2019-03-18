package com.plivo.plivoincomingcall.dagger2;

import com.plivo.plivoincomingcall.screens.dial.DialActivity;
import com.plivo.plivoincomingcall.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.MoreCallsFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.tabs.DialFragment;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AdapterModule.class
}) @Singleton
public interface ViewComponent {
    void inject(LoginActivity activity);
    void inject(DialActivity activity);

    void inject(DialFragment fragment);
    void inject(MoreCallsFragment fragment);
    void inject(OngoingCallFragment fragment);
    void inject(IncomingCallFragment fragment);
}
