package com.plivo.plivoaddressbook.screens.dial.calls;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.DialViewModel;
import com.plivo.plivoaddressbook.screens.dial.tabs.TabFragment;
import com.plivo.plivoaddressbook.utils.TickManager;
import com.plivo.plivoaddressbook.widgets.CallButton;
import com.plivo.plivoaddressbook.widgets.CallTimer;
import com.plivo.plivoaddressbook.widgets.Dialer;

import javax.inject.Inject;

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

public class OngoingCallFragment extends TabFragment {

    private static final String TAG = OngoingCallFragment.class.getSimpleName();

    @BindView(R.id.dtmf_textView)
    AppCompatTextView dtmfTextView;

    @BindView(R.id.contact_name)
    AppCompatTextView nameTextView;

    @BindView(R.id.contact_number)
    AppCompatTextView numberTextView;

    @BindView(R.id.end_call_btn)
    CallButton callBtn;

    @BindView(R.id.mute_btn)
    ToggleButton muteBtn;

    @BindView(R.id.hold_btn)
    ToggleButton holdBtn;

    @BindView(R.id.speaker_btn)
    ToggleButton speakerBtn;

    @BindView(R.id.dialer_btn)
    ToggleButton dialerBtn;

    @BindView(R.id.dialer)
    Dialer dialer;

    @BindView(R.id.timer)
    CallTimer callTimer;

    @BindView(R.id.on_hold_indicator)
    AppCompatTextView holdIndicator;

    @BindView(R.id.number_editText)
    AppCompatEditText numberEditText;

    @BindView(R.id.overlay)
    View carrierCallInProgressOverlay;

    @Inject
    TickManager tickManager;

    private AudioManager audioManager;

    private DialViewModel viewModel;

    private OnDialerClickListener onDialerClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
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
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        viewModel.incomingDTMFObserver().observe(this, digit -> dtmfTextView.setText(dtmfTextView.getText() + " " + digit));
        tickManager.setTickListener(callTimer);

        showState(Call.STATE.IDLE);

        numberEditText.setEnabled(true);

        updateUi(viewModel.getCurrentCall());
    }

    @OnCheckedChanged(R.id.speaker_btn)
    public void onClickSpeakerBtn(CompoundButton btn, boolean isChecked) {
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(isChecked);
    }

    @OnCheckedChanged(R.id.mute_btn)
    public void onClickMuteBtn(CompoundButton btn, boolean isChecked) {
        Log.d(TAG, "onClickMuteBtn " + isChecked);
        if (isChecked) {
            viewModel.mute();
        } else {
            viewModel.unmute();
        }
    }

    @OnCheckedChanged(R.id.hold_btn)
    public void onClickHoldBtn(CompoundButton btn, boolean isChecked) {
        Log.d(TAG, "onClickHoldBtn " + isChecked);
        if (isChecked) {
            viewModel.hold();
        } else {
            viewModel.unHold();
        }
    }

    @OnTextChanged(R.id.number_editText)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s) && before < count) {
            viewModel.sendDTMF(Character.toString(s.charAt(s.length() - 1)));
        }
    }

    @OnClick(R.id.end_call_btn)
    public void onClickCallBtn() {
        if (viewModel.getCurrentCall() == null) return;

        switch (viewModel.getCurrentCall().getState()) {
            case ANSWERED:
            case RINGING:
                viewModel.hangup();
                break;

            case IDLE:
                String phone_num = dialer.getText();
                viewModel.call(Call.newCall(phone_num));
                break;
        }
    }

    @OnClick(R.id.dialer_btn)
    public void onClickDialerBtn() {
        dialer.setVisibility(dialer.isShown() ? View.GONE : View.VISIBLE); // toggle
        if (onDialerClickListener != null) {
            onDialerClickListener.onClickDialer(dialer.isShown());
        }
    }

    @Override
    public void updateUi(Call call) {
        if (isAdded()) {
            ((DialActivity) getActivity()).showLogout(false);
        }

        if (call == null || viewModel == null || !isAdded()) return;

        Log.d(TAG, call.getContact().getPhoneNumber() + " updateUi " + call.getState());
        callBtn.setState(call.getState());

        holdIndicator.setVisibility(View.GONE);
        // todo: color state changes move to CallButton
        switch (call.getState()) {
            case HANGUP:
            case REJECTED:
            case INVALID:
                callBtn.setText("Call");
                callBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                call.setState(Call.STATE.IDLE);
                nameTextView.setText("");
                tickManager.stop(call);
                numberEditText.setEnabled(true);
                break;

            case RINGING:
            case ANSWERED:
                numberEditText.setEnabled(false);
                callBtn.setText("End Call");
                callBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                nameTextView.setText(String.format(getString(call.isIncoming() ? R.string.incoming_from : R.string.outgoing_to), call.getContact().getName(), call.getContact().getPhoneNumber()));
                if (call.getState() == Call.STATE.ANSWERED) {
                    Log.d(".anil", "tick " + call.getTick());
                    tickManager.start(call);
                    if (call.isHold()) holdIndicator.setVisibility(View.VISIBLE);
                }
                break;

            case IDLE:
                callBtn.setText("Call");
                callBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));
                nameTextView.setText("");
                numberEditText.setEnabled(true);
                break;

            default: Log.e(TAG,"Unknown Error"); break;
        }

        showState(call.getState());
        muteBtn.setChecked(call.isMute());
        holdBtn.setChecked(call.isHold());
        speakerBtn.setChecked(audioManager.isSpeakerphoneOn());
        showCarrierInProgressOverlay(viewModel.isCarrierCallInProgress());
    }

    public void showCarrierInProgressOverlay(boolean show) {
        carrierCallInProgressOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        callBtn.setEnabled(!show);
        muteBtn.setEnabled(!show);
        dialerBtn.setEnabled(!show);
        holdBtn.setEnabled(!show);
        speakerBtn.setEnabled(!show);
        viewModel.setCarrierCallInProgress(show);
    }


    private OngoingCallFragment setOnDialerClickListener(OnDialerClickListener onDialerClickListener) {
        this.onDialerClickListener = onDialerClickListener;
        return this;
    }

    public static OngoingCallFragment newInstance(OnDialerClickListener dialerClickListener) {
        return new OngoingCallFragment().setOnDialerClickListener(dialerClickListener);
    }

    public interface OnDialerClickListener {
        void onClickDialer(boolean isShown);
    }
}
