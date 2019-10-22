package com.plivo.plivosimplequickstart;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

public class Utils {
    // endpoint username & password
    static final String USERNAME = "sanity180521094254";
    static final String PASSWORD = "12345";

    static final String HH_MM_SS = "%02d:%02d:%02d";
    static final String MM_SS = "%02d:%02d";

    public static HashMap<String, Object> options = new HashMap<String, Object>()
    {{
        put("enableTracking",true);
    }};

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
