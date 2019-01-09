package com.plivo.endpoint;

class Log {
    private static final String TAG = "PlivoEndpoint";

    public static void D(String l, boolean... force) {
        log(l, android.util.Log.DEBUG);
    }

    public static void E(String l, boolean... force) {
        log(l, android.util.Log.ERROR);
    }

    public static void I(String l, boolean... force) {
        log(l, android.util.Log.INFO);
    }

    public static void log(String l, int priority, boolean... force) {
        if (Log.isEnabled() || (force != null && force.length > 0 && force[0])) {
            android.util.Log.println(priority, TAG, l);
        }
    }

    public static void enable(boolean enable) {
        Global.DEBUG = enable;
    }

    public static boolean isEnabled() {
        return Global.DEBUG;
    }
}

