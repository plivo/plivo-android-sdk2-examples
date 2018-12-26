package com.plivo.endpoint.call.external;

import android.Manifest;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Outgoing;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static com.google.common.truth.Truth.assertThat;
import static com.plivo.endpoint.call.EndpointOutgoingCallTest.ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT;
import static com.plivo.endpoint.login.EndpointLoginTest.LOGIN_TIMEOUT;
import static com.plivo.endpoint.testutils.TestConstants.LOGIN_TEST_ENDPOINT;
import static com.plivo.endpoint.testutils.TestConstants.PLIVO_ENDPOINT_TEST_NUM;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EndpointExternalInvokeTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    );

    @Mock
    EventListener eventListener;

    @Mock
    Outgoing outgoing;

    Endpoint endpoint;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if (endpoint == null) {
            endpoint = Endpoint.newInstance(true, eventListener);
        }
        assertThat(endpoint).isNotNull();

        endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second);
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLogin();

        try {
            outgoing = endpoint.createOutgoingCall();
        } catch (Endpoint.EndpointNotRegisteredException e) {
            e.printStackTrace();
        }
        assertThat(outgoing).isNotNull();
    }

    // Outgoing
    @Test
    public void endpoint_make_outcall_answer_test() {
        assertThat(outgoing.call(PLIVO_ENDPOINT_TEST_NUM)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallAnswered(outgoing);
    }

    @Test
    public void endpoint_make_outcall_answer_hangup_test() {
        assertThat(outgoing.call(PLIVO_ENDPOINT_TEST_NUM)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallAnswered(outgoing);
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallHangup(outgoing);
    }

    @Test
    public void endpoint_make_outcall_reject_test() {
        assertThat(outgoing.call(PLIVO_ENDPOINT_TEST_NUM)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallRejected(outgoing);
    }
}
