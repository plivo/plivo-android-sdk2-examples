# How to use Plivo SDK to Login the endpoint
- Register for a Plivo Account if not already https://console.plivo.com/accounts/register/
- You can get the username from the endpoint section https://console.plivo.com/voice/endpoints/. If no endpoint available, create one https://console.plivo.com/voice/endpoints/add/
- You can change the Password by clicking on the alias of your endpoint and 'Edit Endpoint'
- Clone the PlivoRegistration app https://github.com/plivo-dev/android-sdk-examples/tree/master/PlivoRegistration
- Download the Plivo Beta SDK https://www.plivo.com/docs/sdk/android/v2/ and endpoint.aar or endpoint.jar (helper library) https://www.plivo.com/docs/helpers/java/
- Here https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoRegistration/app/src/main/java/com/plivo/plivoregistration/plivo/layer/PlivoBackend.java plivoEndpoint() creates Endpoint instance (from the plivoendpoint.aar) which loads the Plivo SDK and registers EventListener for the SDK callbacks.
```javascript
    protected Endpoint plivoEndpoint() {
         return endpoint != null? endpoint:
                 (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }
```

- Enter the username and password on the LoginActivity screen.
- When you click on sign in, create a User object with the entered fields and call the PlivoSDK plivoEndpoint().login()
```javascript
    public void login(User user, PlivoBackendListener.LoginListener listener) {
             this.loginListener = listener;
             plivoEndpoint().setRegTimeout(LOGIN_TIMEOUT);
             plivoEndpoint().login(user.getUsername(), user.getPassword());
         }
```

- If login success, onLogin() callback is fired from the SDK. You can go to DialActivity.
```javascript
    @Override
         public void onLogin() {
             if (loginListener != null) loginListener.onLogin(true);
         }
```

- If login failed, onLoginFailed()
```javascript
    @Override
         public void onLoginFailed() {
             if (loginListener != null) loginListener.onLogin(false);
         }
```

- Click on the logout on DialActivity to call SDK plivoEndpoint().logout()
```javascript
    public boolean logout(PlivoBackendListener.LogoutListener listener) {
             this.logoutListener = listener;
             return plivoEndpoint().logout();
         }
```

- onLogout() callback is fired.
```javascript
    @Override
         public void onLogout() {
             if (logoutListener != null) logoutListener.logout();
         }
```
