package com.plivo.voicecalling.Helpers;

import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

/**
 * Created by Siva on 13/06/17.
 */

public interface EndPointListner {

        void onLogin();

        void onLogout();

        void onLoginFailed();

        void onIncomingDigitNotification(String var1);

        void onIncomingCall(Incoming var1);

        void onIncomingCallHangup(Incoming var1);

        void onIncomingCallRejected(Incoming var1);

        void onOutgoingCall(Outgoing var1);

        void onOutgoingCallAnswered(Outgoing var1);

        void onOutgoingCallRejected(Outgoing var1);

        void onOutgoingCallHangup(Outgoing var1);

        void onOutgoingCallInvalid(Outgoing var1);

}
