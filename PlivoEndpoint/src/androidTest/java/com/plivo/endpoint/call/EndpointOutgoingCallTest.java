package com.plivo.endpoint.call;

import android.Manifest;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Outgoing;
import com.plivo.endpoint.testutils.SynchronousExecutor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static com.google.common.truth.Truth.assertThat;
import static com.plivo.endpoint.login.EndpointLoginTest.LOGIN_TIMEOUT;
import static com.plivo.endpoint.testutils.TestConstants.INVALID_TEST_NUM;
import static com.plivo.endpoint.testutils.TestConstants.INVALID_TEST_NUM2;
import static com.plivo.endpoint.testutils.TestConstants.LOGIN_TEST_ENDPOINT;
import static com.plivo.endpoint.testutils.TestConstants.MOBILE_TEST_NUM;
import static com.plivo.endpoint.testutils.TestConstants.PLIVO_ENDPOINT_TEST_NUM;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EndpointOutgoingCallTest {
    public static final long ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    public static final long ON_OUTGOING_REJECT_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(15);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    );

    @Mock
    EventListener eventListener;

    Endpoint endpoint;

    @Mock
    Outgoing outgoing;

    private SynchronousExecutor bkgTask = new SynchronousExecutor();

    @BeforeClass
    public static void create() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if (endpoint == null) {
            endpoint = Endpoint.newInstance(true, eventListener);
        }
        assertThat(endpoint).isNotNull();

        endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second);
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLogin();

        outgoing = endpoint.createOutgoingCall();
        assertThat(outgoing).isNotNull();
    }

    // Outgoing call

    @Test
    public void endpoint_make_outcall_to_plivo_endpoint_test() {
        makeOutCallAndHangupVerify(PLIVO_ENDPOINT_TEST_NUM);
    }

    @Test
    public void endpoint_make_outcall_to_plivo_endpoint_async_test() {
        bkgTask.execute(() -> makeOutCallAndHangupVerify(PLIVO_ENDPOINT_TEST_NUM));
    }

    @Test
    public void endpoint_make_outcall_to_mobile_test() {
        makeOutCallAndHangupVerify(MOBILE_TEST_NUM);
    }

    @Test
    public void endpoint_make_outcall_to_mobile_async_test() {
        bkgTask.execute(() -> makeOutCallAndHangupVerify(MOBILE_TEST_NUM));
    }

    // Needs custom server to test it out.
//    @Test
//    public void endpoint_make_outcall_to_plivo_endpoint_with_custom_headers_test() {
//        Map<String, String> extraHeaders = new HashMap<>();
//        extraHeaders.put("X-PH-Header1", "12345");
//        extraHeaders.put("X-PH-Header2", "34567");
//
//        assertThat(outgoing.callH(PLIVO_ENDPOINT_TEST_NUM, extraHeaders)).isTrue();
//        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
//        hangupOutCallAndVerify();
//    }

    @Test
    public void endpoint_make_outcall_to_plivo_endpoint_with_no_headers_test() {
        assertThat(outgoing.callH(PLIVO_ENDPOINT_TEST_NUM, null)).isFalse();
    }

    @Test
    public void endpoint_make_outcall_to_invalid_endpoint_test() {
        makeOutcallInvalidStateVerify(INVALID_TEST_NUM);
    }

    @Test
    public void endpoint_make_outcall_to_invalid_endpoint_async_test() {
        bkgTask.execute(() -> makeOutcallInvalidStateVerify(INVALID_TEST_NUM));
    }

    @Test
    public void endpoint_make_outcall_to_invalid_endpoint2_test() {
        makeOutcallInvalidNumberVerify(INVALID_TEST_NUM2);
    }

    @Test
    public void endpoint_make_outcall_to_invalid_endpoint2_async_test() {
        bkgTask.execute(() -> makeOutcallInvalidNumberVerify(INVALID_TEST_NUM2));
    }

    private void makeOutCallAndHangupVerify(String num) {
        assertThat(outgoing.call(num)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
        hangupOutCallAndVerify();
    }

    private void hangupOutCallAndVerify() {
        outgoing.hangup();
        verify(eventListener, timeout(ON_OUTGOING_REJECT_CB_RECEIVE_TIMEOUT)).onOutgoingCallHangup(outgoing);
    }

    private void makeOutcallInvalidStateVerify(String num) {
        assertThat(outgoing.call(num)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallRejected(outgoing);
    }

    private void makeOutcallInvalidNumberVerify(String num) {
        assertThat(outgoing.call(num)).isTrue();
        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCallInvalid(outgoing);
    }
}
