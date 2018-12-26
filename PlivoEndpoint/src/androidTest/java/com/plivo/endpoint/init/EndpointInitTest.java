package com.plivo.endpoint.init;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void endpoint_isInitialized_test() {
        loadLib();
    }

    @Test
    public void endpoint_isInitialized_async_test() {
        SynchronousExecutor bkgTask = new SynchronousExecutor();
        bkgTask.execute(() -> loadLib());
    }

    private void loadLib() {
        Endpoint endpoint = Endpoint.newInstance(true, eventListener);
        assertThat(endpoint).isNotNull(); // library .so loaded
    }
}
