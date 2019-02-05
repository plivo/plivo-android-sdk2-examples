package com.plivo.plivoaddressbook.screens.dial.tabs;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.BaseFragment;
import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.DialViewModel;
import com.plivo.plivoaddressbook.utils.AlertUtils;
import com.plivo.plivoaddressbook.utils.NetworkUtils;
import com.plivo.plivoaddressbook.widgets.CallButton;
import com.plivo.plivoaddressbook.widgets.Dialer;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.plivo.plivoaddressbook.model.Call.STATE.ANSWERED;
import static com.plivo.plivoaddressbook.model.Call.STATE.IDLE;
import static com.plivo.plivoaddressbook.model.Call.STATE.RINGING;

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

    public static DialFragment newInstance() { return new DialFragment(); }
}
