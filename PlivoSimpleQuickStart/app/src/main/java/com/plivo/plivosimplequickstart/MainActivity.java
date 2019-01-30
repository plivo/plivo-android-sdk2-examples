package com.plivo.plivosimplequickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

public class MainActivity extends AppCompatActivity implements EventListener {

    enum STATE {
        IDLE,
        RINGING, // ringing after call is outgoing/incoming
        ANSWERED, // outgoing/incoming call is answered
        HANGUP,
        REJECTED,
        INVALID // made a out call to invalid phone number
    }

    View loginView;
    View callView;
    Button callBtn;
    Button rejectBtn;
    Button answerBtn;

    private TextView statusDisplay;

    Endpoint endpoint;

    private STATE state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginView = findViewById(R.id.view_login);
        callView = findViewById(R.id.view_call);
        callBtn = findViewById(R.id.call_btn);
        rejectBtn = findViewById(R.id.reject_btn);
        answerBtn = findViewById(R.id.answer_btn);
        callBtn.setOnClickListener(onCallBtnClickListener);
        answerBtn.setOnClickListener(onAnswerClickListener);
        rejectBtn.setOnClickListener(onRejectClickListener);

        endpoint = Endpoint.newInstance(true, this);
        updateUi(STATE.IDLE);

        // mock
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        username.setText("android1181024115518");
        password.setText("plivo");

    }

    private void toggleLoginViewCallView(boolean showCallView) {
        callView.setVisibility(showCallView ? View.VISIBLE : View.GONE);
        loginView.setVisibility(showCallView ? View.GONE : View.VISIBLE);

        if (!showCallView) {
            findViewById(R.id.login_btn).setOnClickListener(onLoginClickListener);
        }

        if (callView.isShown()) {
            statusDisplay = findViewById(R.id.status);
            findViewById(R.id.logout_btn).setOnClickListener(onLogoutClickListener);
        }
    }

    private void updateUi(STATE state) {
        showState(state);
        switch (state) {
            case IDLE:
                toggleCallBtnGroup();
                toggleLoginViewCallView(true);
                break;

            case RINGING:
                toggleCallBtnGroup();
                break;

            case ANSWERED:
                break;
        }

    }

    private void toggleCallBtnGroup() {
        callBtn.setVisibility(state == STATE.RINGING ? View.GONE : View.VISIBLE);
        rejectBtn.setVisibility(state == STATE.RINGING ? View.VISIBLE : View.GONE);
        answerBtn.setVisibility(state == STATE.RINGING ? View.VISIBLE : View.GONE);
    }

    private void showState(STATE state) {
        this.state = state;
        if (statusDisplay != null) {
            statusDisplay.setText(state.name());
        }
    }

    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);

            endpoint.login(username.getText().toString(), password.getText().toString());
        }
    };

    private View.OnClickListener onLogoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            endpoint.logout();
        }
    };

    private View.OnClickListener onCallBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText numberEditBox = findViewById(R.id.numberBox);
            numberEditBox.setText(Inputs.TEST_CALL_PSTN);

            Outgoing outgoing;
            outgoing = endpoint.createOutgoingCall();

            if (outgoing != null) {
                outgoing.call(numberEditBox.getText().toString());
            }
        }
    };

    private View.OnClickListener onAnswerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener onRejectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            endpoint.logout();
        }
    };

    @Override
    public void onLogin() {
        updateUi(STATE.IDLE);
    }

    @Override
    public void onLogout() {
        updateUi(STATE.IDLE);
    }

    @Override
    public void onLoginFailed() {
        updateUi(STATE.IDLE);
    }

    @Override
    public void onIncomingDigitNotification(String s) {

    }

    @Override
    public void onIncomingCall(Incoming incoming) {
        updateUi(STATE.RINGING);
    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {
        updateUi(STATE.HANGUP);
    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {
        updateUi(STATE.REJECTED);
    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {
        updateUi(STATE.RINGING);
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        updateUi(STATE.ANSWERED);
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        updateUi(STATE.REJECTED);
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        updateUi(STATE.HANGUP);
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        updateUi(STATE.INVALID);
    }
}
