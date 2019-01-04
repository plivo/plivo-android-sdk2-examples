package com.plivo.endpoint;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Utils {
    static final Set<String> VALID_DTMF = new HashSet<>(Arrays.asList(
            new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "#", "*"}
    ));

    private static final String VALID_HEADER_CHARS = "abcdefghijklmnopqrstvwxyzABCDEFGHIJKLMNOPQRSTUVWXTZ0123456789-";
    private static final int MAX_HEADER_KEY_LENGTH = 24;
    private static final int MAX_HEADER_VALUE_LENGTH = 48;

    static String mapToString(Map<String, String> map) {
        if (map == null || map.isEmpty()) return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!TextUtils.isEmpty(stringBuilder)) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey() + ":" + entry.getValue());
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param string: comma separated value of map k1:v1,k2:v2
     * @return
     */
    static Map<String, String> stringToMap(String string) {
        if (TextUtils.isEmpty(string)) return null;

        Map<String, String> map = new HashMap<>();
        String[] keyValuePairs = string.trim().split(",");
        int delimiterIndex;
        for (String kv : keyValuePairs) {
            delimiterIndex = kv.indexOf(":");
            if (delimiterIndex != -1)
                map.put(kv.substring(0, delimiterIndex).trim(), kv.substring(delimiterIndex+1).trim());
        }

        return map;
    }

    // this checks and removes unsupported key value pair,
    // but keeping the same name as before as it is exposed already not to break the backward compatibility.
    static void checkSpecialCharacters(Map<String, String> map) {
        if (map == null || map.isEmpty()) return;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!isValid(entry.getKey()) || !isValid(entry.getValue())) {
                Log.D(entry + " contains characters that aren't allowed");
                map.remove(entry.getKey());
            }

            if (!isSupported(entry)) {
                Log.D("Skipping " + entry);
                map.remove(entry.getKey());
            }
        }

    }

    private static boolean isSupported(Map.Entry<String, String> entry) {
        if (entry == null || TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) return false;

        return (entry.getKey().startsWith("X-PH-") || entry.getKey().startsWith("X-Ph-")) &&
                entry.getKey().length() <= MAX_HEADER_KEY_LENGTH &&
                entry.getValue().length() <= MAX_HEADER_VALUE_LENGTH;
    }

    private static boolean isValid(String string) {
        if (TextUtils.isEmpty(string)) return false;

        for (int i=0; i< string.length(); i++){
            if (!VALID_HEADER_CHARS.contains(String.valueOf(string.charAt(i)) )) {
                return false;
            }
        }
        return true;
    }
}

