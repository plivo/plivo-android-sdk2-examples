package com.plivo.plivoincomingcall.screens.dial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoincomingcall.BaseFragment;
import com.plivo.plivoincomingcall.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallFragment extends BaseFragment {
    private DialViewModel viewModel;

    @BindView(R.id.incoming_textView)
    AppCompatTextView incomingTextview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
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
        updateUi();
    }

    @Override
    public void updateUi() {
        super.updateUi();
        if (viewModel == null || !isAdded()) {
            return;
        }
        incomingTextview.setText(String.format(getString(R.string.incoming_from), viewModel.getCurrentCall().getFrom()));
    }

    @OnClick(R.id.answer)
    public void onClickAnswer() {
        viewModel.answer();
    }

    @OnClick(R.id.reject)
    public void onClickReject() {
        viewModel.reject();
    }

    public static IncomingCallFragment newInstance() {
        return new IncomingCallFragment();
    }
}
