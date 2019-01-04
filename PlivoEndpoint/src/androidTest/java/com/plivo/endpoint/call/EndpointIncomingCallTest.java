package com.plivo.endpoint.call;

import android.Manifest;

import com.plivo.endpoint.BackendListener;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Global;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.testutils.SynchronousExecutor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static com.google.common.truth.Truth.assertThat;
import static com.plivo.endpoint.login.EndpointLoginTest.LOGIN_TIMEOUT;
import static com.plivo.endpoint.testutils.TestConstants.LOGIN_TEST_ENDPOINT;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EndpointIncomingCallTest {

    public static final long ON_INCOMING_CALL_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    public static final long ON_INCOMING_REJECT_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(15);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    );

    @Mock
    EventListener eventListener;

    @Mock
    BackendListener backendListener;

    Endpoint endpoint;

    private SynchronousExecutor bkgTask = new SynchronousExecutor();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if (endpoint == null) {
            endpoint = Endpoint.newInstance(true, eventListener);
        }
        assertThat(endpoint).isNotNull();

        if (backendListener == null) {
            backendListener = new BackendListener(Global.DEBUG, endpoint, eventListener);
        }

        endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second);
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLogin();
    }

    @Test
    public void endpoint_receive_incoming_call_test() {
        verify(backendListener, timeout(ON_INCOMING_CALL_CB_RECEIVE_TIMEOUT)).onDebugMessage("[backend-logs][onDebugMessage]on_incoming_call");
    }

    @Test
    public void endpoint_receive_incoming_call_async_test() {
        bkgTask.execute(() -> verify(backendListener, timeout(ON_INCOMING_CALL_CB_RECEIVE_TIMEOUT)).onDebugMessage("[backend-logs][onDebugMessage]on_incoming_call"));
    }
}
