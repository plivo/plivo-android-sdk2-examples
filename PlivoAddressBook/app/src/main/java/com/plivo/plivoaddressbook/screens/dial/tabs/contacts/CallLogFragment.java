package com.plivo.plivoaddressbook.screens.dial.tabs.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.adapters.CallLogAdapter;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.tabs.TabFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends TabFragment implements CallLogAdapter.OnItemClickListener {
    @BindView(R.id.call_log_recyclerView)
    RecyclerView recyclerView;

    @Inject
    CallLogAdapter callLogAdapter;

    private ContactViewModel viewModel;

    private OnClickCallLogListener onClickCallLogListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
    }

    @Override
    public String getTitle() {
        return "Recents";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        callLogAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(callLogAdapter);

        viewModel.callLogObserver().observe(this, calls -> {
            callLogAdapter.setItems(calls);
        });
    }

    @Override
    public void updateUi(Call call) {
        super.updateUi(call);
        viewModel.getCallLog();
    }

    @Override
    public void onItemClick(Call selected) {
        if (onClickCallLogListener != null) onClickCallLogListener.onClickCall(selected);
    }

    private CallLogFragment setOnClickCallLogListener(OnClickCallLogListener onClickCallLogListener) {
        this.onClickCallLogListener = onClickCallLogListener;
        return this;
    }

    public static CallLogFragment newInstance(OnClickCallLogListener actionListener) {
        return new CallLogFragment().setOnClickCallLogListener(actionListener);
    }

    public interface OnClickCallLogListener {
        void onClickCall(Call selected);
    }

}
