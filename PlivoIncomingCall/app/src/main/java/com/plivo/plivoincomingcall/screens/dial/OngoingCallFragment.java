package com.plivo.plivoincomingcall.screens.dial;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.plivo.plivoincomingcall.BaseFragment;
import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.layer.plivo.PlivoCall;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.widgets.CallButton;
import com.plivo.plivoincomingcall.widgets.Dialer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class OngoingCallFragment extends BaseFragment {

    private static final String TAG = OngoingCallFragment.class.getSimpleName();

    @BindView(R.id.username)
    AppCompatTextView usernameTextView;

    @BindView(R.id.number_editText)
    AppCompatEditText numberEditText;

    @BindView(R.id.dtmf_textView)
    AppCompatTextView dtmfTextView;

    @BindView(R.id.incoming_textView)
    AppCompatTextView incomingTextView;

    @BindView(R.id.call_btn)
    CallButton call_btn;

    @BindView(R.id.dialer)
    Dialer dialer;

    private DialViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ongoing_call, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        viewModel.incomingCallObserver().observe(this, call -> updateUi(call));
        viewModel.incomingDTMFObserver().observe(this, digit -> dtmfTextView.setText(dtmfTextView.getText() + " " + digit));
        dialer.setOnDigitClickListener(digit -> viewModel.sendDTMF(digit));

        setTitle("Call IDLE");
        usernameTextView.setText("loggedIn " + viewModel.getLoggedInUser().getUsername());
    }

    @OnCheckedChanged(R.id.mute_btn)
    public void onClickMuteBtn(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            viewModel.mute();
        } else {
            viewModel.unmute();
        }
    }

    @OnTextChanged(R.id.number_editText)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) call_btn.setState(PlivoCall.CALL_STATE.IDLE);
    }

    @OnClick(R.id.call_btn)
    public void onClickCallBtn() {
        switch (call_btn.getState()) {
            case ANSWERED:
                endCall();
                break;
            default:
                break;
        }

    }

    private void endCall() {
        viewModel.hangup();
    }

    @Override
    public void updateUi() {
        super.updateUi();
        if (viewModel == null || !isAdded()) {
            return;
        }
        updateUi(viewModel.getCurrentCall());
    }

    private void updateUi(Call call) {
        if (call == null || viewModel == null || !isAdded()) return;

        Log.d(TAG, "updateUi " + call.getState());
        call_btn.setState(call.getState());

        // todo: color state changes move to CallButton
        switch (call.getState()) {
            case HANGUP:
            case REJECTED:
            case INVALID:
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                call.setState(PlivoCall.CALL_STATE.IDLE);
                incomingTextView.setText("");
                break;

            case RINGING:
            case ANSWERED:
                call_btn.setText("End Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                if (call.isIncoming()) {
                    incomingTextView.setText(String.format(getString(R.string.incoming_from), viewModel.getCurrentCall().getFrom()));
                } else if (call.isOutgoing()) {
                    incomingTextView.setText(String.format(getString(R.string.outgoing_to), viewModel.getCurrentCall().getTo()));
                }
                break;

            case IDLE:
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));
                incomingTextView.setText("");
                break;

            default: Log.e(TAG,"Unknown Error"); break;
        }

        setTitle("Call " + call.getState());
    }

    public static OngoingCallFragment newInstance() {
        return new OngoingCallFragment();
    }
}
