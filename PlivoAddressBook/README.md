# How to use Plivo SDK to make call from Contacts or Call log
- Check for PlivoRegistration App if not already registered https://github.com/plivo/plivo-android-sdk2-examples/tree/master/PlivoRegistration/README.md
- Clone the PlivoAddressBook app https://github.com/plivo/plivo-android-sdk2-examples/tree/master/PlivoAddressBook
- Download the Plivo SDK PlivoEndpoint.aar from section 'Getting started with Plivo Android SDK' here https://www.plivo.com/docs/sdk/client-side-sdks/android/overview/
- Here https://github.com/plivo/plivo-android-sdk2-examples/tree/master/PlivoAddressBook/app/src/main/java/com/plivo/plivoAddressBook/layer/impl/PlivoSDKImpl.java endpoint() creates Endpoint instance (from the plivoendpoint.aar) which loads the Plivo SDK and registers EventListener for the SDK callbacks.
```javascript
    protected Endpoint endpoint() {
         return endpoint != null? endpoint:
                 (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }
```

- After login, you can click on / swipe left to "Recents", to go into call logs and click on an item to callback.
- After login, you can click on / swipe right to "Contacts", to go into Contacts and click on an item to make an outgoing call.
- And the outgoing call is same as PlivoOutgoing app. When you click on call, plivoEndpoint().createOutgoingCall() creates an Outgoing instance (from the endpoint.aar)
```javascript
    private Outgoing createOutgoing() {
        return endpoint().createOutgoingCall();
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

