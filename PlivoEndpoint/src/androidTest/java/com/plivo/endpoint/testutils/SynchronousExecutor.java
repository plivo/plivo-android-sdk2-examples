package com.plivo.endpoint.testutils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class SynchronousExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
