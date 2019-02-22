# How to use Plivo SDK to make a outgoing call
- Check for PlivoRegistration App if not already registered https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoRegistration/README.md
- Clone the PlivoOutgoingCall app https://github.com/plivo-dev/android-sdk-examples/tree/master/PlivoOutgoingCall
- Download the Plivo Beta SDK https://www.plivo.com/docs/sdk/android/v2/ and endpoint.aar or endpoint.jar (helper library) https://www.plivo.com/docs/helpers/java/
- Here https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoOutgoingCall/app/src/main/java/com/plivo/plivoOutgoingCall/plivo/layer/PlivoBackend.java plivoEndpoint() creates Endpoint instance (from the plivoendpoint.aar) which loads the Plivo SDK and registers EventListener for the SDK callbacks.
```javascript
    protected Endpoint plivoEndpoint() {
         return endpoint != null? endpoint:
                 (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }
```

- Enter the username and password of your Plivo account on the LoginActivity screen.
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

- You can dial a number on this screen on the dial activity, when you click on call, plivoEndpoint().createOutgoingCall() creates an Outgoing instance (from the endpoint.aar)
```javascript
    private Outgoing outgoing() throws EndpointNotRegisteredException {
            if (outgoing == null) {
                outgoing = plivoEndpoint().createOutgoingCall();
            }
    
            return outgoing;
        }
```

- And, you can make a outgoing call with outgoing().call(number) which places a SIP call through the SDK.
```javascript
    public boolean outCall(String number, PlivoBackendListener.CallListener listener) throws EndpointNotRegisteredException {
            this.callListener = listener;
            return outgoing().call(number);
        }
```

- Then, SDK fires the onOutgoingCall() callback, where you can set states and notify your UI.
```javascript
    @Override
        public void onOutgoingCall(Outgoing outgoing) {
            this.outgoing = outgoing;
            outCallState = PlivoCallState.OUT_CALL_STATE.RINGING;
            notifyCallState();
        }
```

