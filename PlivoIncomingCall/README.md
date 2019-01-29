# How to use Plivo SDK to receive incoming call even while background
- Check for PlivoRegistration App if not already registered https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoRegistration/README.md
- Clone the PlivoIncomingCall app https://github.com/plivo-dev/android-sdk-examples/tree/master/PlivoIncomingCall
- Download the Plivo Beta SDK https://www.plivo.com/docs/sdk/android/v2/ and endpoint.aar or endpoint.jar (helper library) https://www.plivo.com/docs/helpers/java/
- Here https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoIncomingCall/app/src/main/java/com/plivo/plivoIncomingCall/layer/impl/PlivoSDKImpl.java plivoEndpoint() creates Endpoint instance (from the plivoendpoint.aar) which loads the Plivo SDK and registers EventListener for the SDK callbacks.
```javascript
    protected Endpoint plivoEndpoint() {
         return endpoint != null? endpoint:
                 (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }
```

- Now, you can make an incoming call to the logged in end point from external sources like PlivoWeb https://s3.amazonaws.com/plivowebrtc/v2-0.html
- When call is received, Plivo SDK fires onIncomingCall() callback. You can make a Call object (or, your own model) and notifiy UI.
```javascript
    @Override
        public void onIncomingCall(Incoming incoming) {
            Call inCall = new Call.Builder()
                    .setId(incoming.getCallId())
                    .setType(PlivoCall.CALL_TYPE.INCOMING)
                    .setState(PlivoCall.CALL_STATE.RINGING)
                    .setFromContact(incoming.getFromContact())
                    .setFromSip(incoming.getFromSip())
                    .setToContact(incoming.getToContact())
                    .setToSip(incoming.getToSip())
                    .setData(incoming)
                    .build();
            setCurrentCall(inCall);
            notifyIncomingCall();
        }
```

- When the app is backgrounded, you can handle background calls by creating a service like here https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoIncomingCall/app/src/main/java/com/plivo/plivoIncomingCall/service/PlivoBackgroundService.java
- Also, you can use this service to check on endpoint login expiry so that endpoint is always logged in to receive calls. You can call SDK keepAlive() which will revive the login (currently keepAlive() on SDK is in progress), so force loging in for now.
```javascript
    private void observeLogin() {
            if (preferencesUtils.isLoginExpired()) {
                backend.keepAlive(success -> {
                    if (!success) {
                        // force login
                        User loggedInUser = preferencesUtils.getUser();
                        if (loggedInUser != null) {
                            backend.login(loggedInUser, onLoginSuccess -> {});
                        }
                    }
                });
            }
        }
    
        private void observeIncomingCall() {
            backend.setIncomingCallListener(state -> {
                startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Constants.EXTRA_INCOMING_CALL, true)
                );
            });
        }
        
        public void keepAlive(PlivoBackendListener.LoginListener listener) {
                this.loginListener = listener;
                endpoint().keepAlive();
            }
```

- Also, you can use AlarmManager to make a repeating alarm to your app on every LOGIN_TIMEOUT, and revive the service which inturn checks for the login expiry. https://github.com/plivo-dev/android-sdk-examples/blob/master/PlivoIncomingCall/app/src/main/java/com/plivo/plivoIncomingCall/receivers/StarterServiceReceiver.java
```javascript
    StarterServiceReceiver.java
    @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) return;
            Log.d(TAG, "onReceive " + intent.getAction());
    
            switch (intent.getAction()) {
                case ACTION_BOOT_COMPLETED:
                case Constants.ACTION_ALARM_RECEIVED:
                    ((App) context.getApplicationContext()).startBakgroundService();
                    break;
            }
        }
        
    AlarmUtils.java
    public void setRepeatingAlarmNow() {
            alarmManager()
                    .setRepeating(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),
                            TimeUnit.SECONDS.toMillis(PreferencesUtils.LOGIN_TIMEOUT),
                            getAlarmIntent());
        }
```

