package com.plivo.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Utils {
    static final Set<String> VALID_DTMF = new HashSet<String>(Arrays.asList(
            new String[] {"0","1","2","3", "4", "5", "6", "7", "8", "9", "#", "*"}
    ));

    static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            String value = map.get(key);
            stringBuilder.append(key);
            stringBuilder.append(":");
            stringBuilder.append(value);
        }
        return stringBuilder.toString();
    }
}

