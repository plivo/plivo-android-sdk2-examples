package com.plivo.endpoint;

class Log {

    public static void log(String l, boolean... force) {
        if (Log.isEnabled() || (force != null && force.length > 0 && force[0])) {
            System.out.println(l);
        }
    }

    public static void enable(boolean enable) {
        Global.DEBUG = enable;
    }

    public static boolean isEnabled() {
        return Global.DEBUG;
    }
}

