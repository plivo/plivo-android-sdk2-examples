package com.plivo.plivoaddressbook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.User;

import java.util.concurrent.TimeUnit;

public class PreferencesUtils {
    private static final String PREFERENCES = "UserPreferences";
    public static final int LOGIN_TIMEOUT = (int) TimeUnit.MINUTES.toSeconds(10);

    private static final String KEY_LOGIN_TIMESTAMP = "KEY_LOGIN_TIMESTAMP";
    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_CARRIER_CALL = "KEY_CARRIER_CALL";

    private Context context;
    private Gson gson;

    private SharedPreferences sharedPreferences;

    public PreferencesUtils(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    private SharedPreferences preferences() {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public void setLogin(boolean notLogout, User... user) {
        long timeStamp = System.currentTimeMillis();
        if (notLogout) {
            // login
            saveUser(user[0]);
        } else {
            // logout
            timeStamp -= TimeUnit.SECONDS.toMillis(LOGIN_TIMEOUT);
            deleteUser();
        }
        preferences().edit()
                .putLong(KEY_LOGIN_TIMESTAMP, timeStamp)
                .commit();
    }

    public boolean isLoginExpired() {
        long loggedInTimeStamp = preferences().getLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis());
        long diff = System.currentTimeMillis() - loggedInTimeStamp;

        return diff > loginTimeout();
    }

    private long loginTimeout() {
        return TimeUnit.SECONDS.toMillis(LOGIN_TIMEOUT) - TimeUnit.MINUTES.toMillis(1); // 1 min defer
    }

    public void saveUser(User user) {
        preferences().edit()
                .putString(KEY_USER, gson.toJson(user))
                .commit();
    }

    public User getUser() {
        if (!preferences().contains(KEY_USER)) return null;
        return gson.fromJson(preferences().getString(KEY_USER, new JsonObject().toString()), User.class);
    }

    private void deleteUser() {
        if (preferences().contains(KEY_USER)) {
            preferences().edit().remove(KEY_USER).commit();
        }
    }

    public void setIsCarrierCallInProgress(boolean inProgress) {
        preferences().edit()
                .putBoolean(KEY_CARRIER_CALL, inProgress)
                .commit();
    }

    public boolean isCarrierCallInProgress() {
        return preferences().getBoolean(KEY_CARRIER_CALL, false);
    }
}
