package com.plivo.plivoaddressbook.screens.dial.calls;

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

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallFragment extends BaseFragment {
    private DialViewModel viewModel;

    @BindView(R.id.incoming_textView)
    AppCompatTextView incomingTextview;

    @BindView(R.id.contact_circle)
    AppCompatTextView incomingCircle;

    @Inject
    NetworkUtils networkUtils;

    @Inject
    AlertUtils alertUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incoming_call, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        updateUi(viewModel.getCurrentCall());
    }

    @Override
    public void updateUi(Call call) {
        super.updateUi(call);
        if (isAdded()) {
            ((DialActivity) getActivity()).showLogout(false);
        }

        if (call == null || viewModel == null || !isAdded()) {
            return;
        }
        incomingTextview.setText(String.format(getString(R.string.incoming_from), call.getContact().getName(), call.getContact().getPhoneNumber()));
        incomingCircle.setText(call.getContact().getName().substring(0,1));
        showState(call.getState());
    }

    @OnClick(R.id.answer)
    public void onClickAnswer() {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            return;
        }
        viewModel.answer();
    }

    @OnClick(R.id.reject)
    public void onClickReject() {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            return;
        }
        viewModel.reject();
    }

    public static IncomingCallFragment newInstance() {
        return new IncomingCallFragment();
    }
}
