package com.plivo.endpoint.login;

import android.util.Pair;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
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
import static com.plivo.endpoint.testutils.TestConstants.LOGIN_TEST_ENDPOINT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EndpointLoginTest {
    public static final long LOGIN_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long ASYNC_LOGIN_TIMEOUT = TimeUnit.SECONDS.toMillis(20);
    private static final long ASYNC_LOGOUT_TIMEOUT = TimeUnit.SECONDS.toMillis(2);

    @Mock
    private EventListener eventListener;

    private Endpoint endpoint;

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
    }

    @Test
    public void endpoint_isInitialized_test() {
        assertThat(endpoint).isNotNull();
    }

    // Login
    @Test
    public void endpoint_login_success_test() {
        endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second);
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLogin();
    }

    @Test
    public void endpoint_login_failure_test() {
        endpoint.login("BLAH_BLAH", "12345");
        verify(eventListener, timeout(LOGIN_TIMEOUT)).onLoginFailed();
    }

    @Test
    public void endpoint_async_login_success_test() {
        bkgTask.execute(() -> endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second));
        verify(eventListener, timeout(ASYNC_LOGIN_TIMEOUT)).onLogin();
    }

    @Test
    public void endpoint_async_login_failure_test() {
        bkgTask.execute(() -> endpoint.login("BLAH_BLAH", "12345"));
        verify(eventListener, timeout(ASYNC_LOGIN_TIMEOUT)).onLoginFailed();
    }


    // Logout
    @Test
    public void endpoint_logout_success_test() {
        assertThat(endpoint.logout()).isTrue();
        verify(eventListener).onLogout();
    }

    @Test
    public void endpoint_async_logout_success_test() {
        bkgTask.execute(() -> endpoint.logout());
        verify(eventListener, timeout(ASYNC_LOGOUT_TIMEOUT)).onLogout();
    }

    // Registered
    @Test
    public void endpoint_isRegistered_test() {
        endpoint.login(LOGIN_TEST_ENDPOINT.first, LOGIN_TEST_ENDPOINT.second);
        verify(eventListener, timeout(ASYNC_LOGIN_TIMEOUT)).onLogin();

        assertThat(endpoint.isRegistered()).isTrue();
    }

    @Test
    public void endpoint_isNotRegistered_test() {
        assertThat(endpoint.logout()).isTrue();
        assertThat(endpoint.isRegistered()).isFalse();
    }
}
