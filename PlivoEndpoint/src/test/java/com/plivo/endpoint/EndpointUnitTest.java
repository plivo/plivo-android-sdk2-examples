package com.plivo.endpoint;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.CharMatcher.is;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class EndpointUnitTest {

    private Endpoint endpoint;

    @Before
    public void init() {
        endpoint = Endpoint.newInstance(true, new EventListener() {
            @Override
            public void onLogin() {

            }

            @Override
            public void onLogout() {

            }

            @Override
            public void onLoginFailed() {

            }

            @Override
            public void onIncomingDigitNotification(String digit) {

            }

            @Override
            public void onIncomingCall(Incoming incoming) {

            }

            @Override
            public void onIncomingCallHangup(Incoming incoming) {

            }

            @Override
            public void onIncomingCallRejected(Incoming incoming) {

            }

            @Override
            public void onOutgoingCall(Outgoing outgoing) {

            }

            @Override
            public void onOutgoingCallAnswered(Outgoing outgoing) {

            }

            @Override
            public void onOutgoingCallRejected(Outgoing outgoing) {

            }

            @Override
            public void onOutgoingCallHangup(Outgoing outgoing) {

            }

            @Override
            public void onOutgoingCallInvalid(Outgoing outgoing) {

            }
        });
    }

    @Test
    public void Endpoint_Object_isNotNull() {
        assertThat(endpoint).isNotNull();
    }
}
