package com.plivo.endpoint.testutils;

import android.util.Pair;

public class TestConstants {
    public static final Pair<String, String> LOGIN_TEST_ENDPOINT = new Pair<>("EPEIGHT180829100349", "12345");
    public static final String PLIVO_ENDPOINT_TEST_NUM = "android1181024115518"; // todo: use original test endpoint
    public static final String MOBILE_TEST_NUM = "+918660031281"; // todo: use original test number
    public static final String INVALID_TEST_NUM = "080123456789"; // invalid state call.status>=480 && <=489
    public static final String INVALID_TEST_NUM2 = "123456789"; // invalid number call.status 404 || 408
    // todo: find and INVALID_TEST_NUM2 . current 123456789 is giving 480
}
