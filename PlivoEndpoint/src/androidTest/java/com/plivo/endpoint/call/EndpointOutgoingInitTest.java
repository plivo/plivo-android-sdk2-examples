package com.plivo.endpoint.call;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Outgoing;
import com.plivo.endpoint.testutils.SynchronousExecutor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static com.plivo.endpoint.login.EndpointLoginTest.LOGIN_TIMEOUT;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EndpointOutgoingInitTest {

    @Mock
    private EventListener eventListener;

    private Endpoint endpoint;
    private Outgoing outgoing;

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
    }

    // Outgoing init
    @Test
    public void endpoint_create_outgoing() {
        try {
            outgoing = endpoint.createOutgoingCall();
        } catch (Endpoint.EndpointNotRegisteredException e) {
            e.printStackTrace();
        }
        assertThat(outgoing).isNotNull();
    }

    @Test(expected = Endpoint.EndpointNotRegisteredException.class)
    public void endpoint_create_outgoing_exception() throws Endpoint.EndpointNotRegisteredException {
        endpoint.logout();
        endpoint.createOutgoingCall();
    }
}
