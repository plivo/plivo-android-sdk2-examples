package com.plivo.plivosimplequickstart;

import android.app.Application;

import com.plivo.endpoint.Endpoint;

public class App extends Application {

    Endpoint plivoEndpoint;

    public Endpoint getPlivoEndpoint() {
        return plivoEndpoint;
    }

    public void setPlivoEndpoint(Endpoint plivoEndpoint) {
        this.plivoEndpoint = plivoEndpoint;
    }
}
