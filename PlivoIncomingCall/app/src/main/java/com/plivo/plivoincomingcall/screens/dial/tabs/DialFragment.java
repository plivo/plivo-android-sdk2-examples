package com.plivo.plivoincomingcall.screens.dial.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.screens.dial.DialActivity;
import com.plivo.plivoincomingcall.screens.dial.DialViewModel;
import com.plivo.plivoincomingcall.utils.AlertUtils;
import com.plivo.plivoincomingcall.utils.NetworkUtils;
import com.plivo.plivoincomingcall.widgets.CallButton;
import com.plivo.plivoincomingcall.widgets.Dialer;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialFragment extends TabFragment {
    @BindView(R.id.username)
    AppCompatTextView usernameTextView;

    @BindView(R.id.call_btn)
    CallButton callBtn;

    @BindView(R.id.dialer)
    Dialer dialer;

    @Inject
    NetworkUtils networkUtils;

    @Inject
    AlertUtils alertUtils;

    private DialViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dial, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        usernameTextView.setText("User: " + viewModel.getLoggedInUser().getUsername());
    }

    @Override
    public String getTitle() {
        return "Dial";
    }

    @OnClick(R.id.call_btn)
    public void onClickCallBtn() {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            return;
        }
        callBtn.setEnabled(false);
        switch (callBtn.getState()) {
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

    @Override
    public void updateUi(Call call) {
        super.updateUi(call);
        if (callBtn != null) {
            callBtn.setEnabled(true);
        }
    }

    public static DialFragment newInstance() { return new DialFragment(); }
}
