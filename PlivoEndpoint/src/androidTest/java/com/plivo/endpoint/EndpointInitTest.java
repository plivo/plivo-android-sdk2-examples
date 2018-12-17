package com.plivo.endpoint;

import com.plivo.endpoint.testutils.SynchronousExecutor;

import org.junit.Before;
import org.junit.Test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EndpointInitTest {

    @Mock
    private EventListener eventListener;

    private Endpoint endpoint;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if (endpoint == null) {
            endpoint = Endpoint.newInstance(true, eventListener);
        }
    }

    @Test
    public void endpoint_isInitialized() {
        assertThat(endpoint).isNotNull();
    }
}
