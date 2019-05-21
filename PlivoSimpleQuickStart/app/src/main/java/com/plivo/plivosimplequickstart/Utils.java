package com.plivo.plivosimplequickstart;

import android.text.TextUtils;

public class Utils {
    static final String INCOMING_CALL_DATA = "INCOMING_CALL_DATA";

    static final String USERNAME = "anand806893466247604717";
    static final String PASSWORD = "12345";

    static final String HH_MM_SS = "%02d:%02d:%02d";
    static final String MM_SS = "%02d:%02d";

    static String from(String fromContact, String fromSip) {
        String from = TextUtils.isEmpty(fromContact)?
                TextUtils.isEmpty(fromSip)? "" : fromSip:
                fromContact;
        return from.contains("\"") ?
                from.substring(from.indexOf("\"")+1, from.lastIndexOf("\"")):
                from;

    }

    static String to(String toSip) {
        return TextUtils.isEmpty(toSip) ? "" :
                toSip.substring(toSip.indexOf(":")+1, toSip.indexOf("@"));
    }
}
