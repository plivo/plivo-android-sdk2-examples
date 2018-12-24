package com.plivo.endpoint.call;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Outgoing;
import com.plivo.endpoint.testutils.SynchronousExecutor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import javax.annotation.meta.When;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static com.plivo.endpoint.login.EndpointLoginTest.LOGIN_TIMEOUT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EndpointOutgoingCallTest {
    private static final long ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    private static final long ON_OUTGOING_REJECT_CB_RECEIVE_TIMEOUT = TimeUnit.SECONDS.toMillis(5);

    @Mock
    EventListener eventListener;

    Endpoint endpoint;

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

        endpoint.login("EPEIGHT180829100349", "12345");
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLogin();

        try {
            outgoing = endpoint.createOutgoingCall();
        } catch (Endpoint.EndpointNotRegisteredException e) {
            e.printStackTrace();
        }
        assertThat(outgoing).isNotNull();
    }

    // Outgoing call

    @Test
    public void endpoint_make_outcall() {
        String num = "android2181024115535";
        validateAndVerify(num);
    }

    @Test
    public void endpoint_make_outcall1() {
        String num = "+918660031281";
        validateAndVerify(num);
    }

    private void validateAndVerify(String num) {
        boolean success = outgoing.call(num);
        assertThat(success).isTrue();

        verify(eventListener, timeout(ON_OUTGOING_CALL_CB_RECEIVE_TIMEOUT)).onOutgoingCall(outgoing);
    }
}
