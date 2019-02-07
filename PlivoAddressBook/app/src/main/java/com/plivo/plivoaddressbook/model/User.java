package com.plivo.plivoaddressbook.model;

public class User {
    private final String deviceToken;
    private final String username;
    private final String password;

    private User(String email, String password, String deviceToken) {
        this.username = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public static class Builder {
        private String username;
        private String password;
        private String deviceToken;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(this.username, this.password, this.deviceToken);
        }

        public Builder setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
            return this;
        }

    }
}
