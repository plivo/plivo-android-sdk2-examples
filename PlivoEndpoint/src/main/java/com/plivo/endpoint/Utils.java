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

    static void checkSpecialCharacters(Map<String, String> map) {
        int header_length = map.size();
        if (header_length > 0) {
            String words = "abcdefghijklmnopqrstvwxyzABCDEFGHIJKLMNOPQRSTUVWXTZ0123456789-";
            for (String key : map.keySet()) {
                String value = map.get(key);
                for(int i=0; i< key.length(); i++){
                    if (!words.contains(String.valueOf(key.charAt(i)) )) {
                        System.out.println(key + ":" + value + " contains characters that aren't allowed");
                        map.remove(key);
                        key = null;
                        break;
                    }
                }
                if (key == null)
                    continue;
                for(int i=0; i< value.length(); i++){
                    if (!words.contains(String.valueOf(value.charAt(i)) )) {
                        System.out.println(key + ":" + value + " contains characters that aren't allowed");
                        map.remove(key);
                        break;
                    }
                }

                if ((!key.startsWith("X-PH-")  && !key.startsWith("X-Ph-")) || (key.length() > 24) ||
                        (value.length() > 48)) {
                    System.out.println("Skipping " + key + ":" + value);
                    map.remove(key);
                }
            }
        }

    }
}

