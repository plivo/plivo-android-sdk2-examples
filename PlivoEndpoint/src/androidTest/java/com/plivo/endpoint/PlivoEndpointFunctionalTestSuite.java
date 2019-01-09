package com.plivo.endpoint;

import com.plivo.endpoint.call.EndpointIncomingCallTest;
import com.plivo.endpoint.call.EndpointOutgoingCallTest;
import com.plivo.endpoint.call.EndpointOutgoingInitTest;
import com.plivo.endpoint.call.external.EndpointExternalInvokeTest;
import com.plivo.endpoint.init.EndpointInitTest;
import com.plivo.endpoint.login.EndpointLoginTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EndpointInitTest.class,
        EndpointLoginTest.class,
        EndpointOutgoingInitTest.class,
        EndpointOutgoingCallTest.class,
        EndpointIncomingCallTest.class,
//        EndpointExternalInvokeTest.class // run this only if you want to test external actions like outgoing answer, reject, etc to invoke manually
})
public class PlivoEndpointFunctionalTestSuite {
}
