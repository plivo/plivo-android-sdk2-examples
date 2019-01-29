package com.plivo.plivoaddressbook.screens.dial.calls;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.BaseFragment;
import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.adapters.MoreCallsAdapter;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.screens.dial.DialActivity;
import com.plivo.plivoaddressbook.screens.dial.DialViewModel;
import com.plivo.plivoaddressbook.utils.AlertUtils;
import com.plivo.plivoaddressbook.utils.TickManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreCallsFragment extends BaseFragment implements MoreCallsAdapter.OnItemClickListener {
    private static final String TAG = MoreCallsFragment.class.getSimpleName();

    @BindView(R.id.callsListView)
    RecyclerView recyclerView;

    @BindView(R.id.other_calls)
    AppCompatTextView otherCallsStrip;

    @Inject
    MoreCallsAdapter callsAdapter;

    @Inject
    TickManager tickManager;

    @Inject
    AlertUtils alertUtils;

    private DialViewModel viewModel;

    private OnSelectCallListener onSelectCallListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_calls, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
        callsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(callsAdapter);

        updateUi(viewModel.getCurrentCall());
    }

    @Override
    public void updateUi(Call call) {
        super.updateUi(call);
        if (call == null || viewModel == null || !isAdded()) {
            return;
        }
        callsAdapter.setItems(viewModel.getOtherCalls());
        getActivity().setTitle("Call SELECT");
    }

    @Override
    public void onItemClick(Call item) {
        handleCurrentCall(viewModel.getCurrentCall());
        viewModel.setCall(item);
        handleSelectedCall(item);

        if (onSelectCallListener != null) {
            onSelectCallListener.onSelectCall(item);
        }
    }

    private void handleCurrentCall(Call call) {
        tickManager.pause(call);
        viewModel.hold();
        alertUtils.showToast(call.getContact().getName() + " on hold");
    }

    private void handleSelectedCall(Call call) {
        tickManager.start(call);
        viewModel.unHold();
    }

    @OnClick(R.id.other_calls)
    public void onClickOtherCalls() {
        showOtherCallsList(true);
    }

    public void showOtherCallsList(boolean show) {
        Log.d(TAG, "showOtherCallsList " + show);
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
            Log.d(TAG, "recyclerView set");
        }
        if (otherCallsStrip != null) {
            otherCallsStrip.setVisibility(show ? View.GONE : View.VISIBLE);
            Log.d(TAG, "otherCallsStrip set");
        }
    }

    private MoreCallsFragment setOnSelectCallListener(OnSelectCallListener onSelectCallListener) {
        this.onSelectCallListener = onSelectCallListener;
        return this;
    }

    public static MoreCallsFragment newInstance(OnSelectCallListener actionListener) {
        return new MoreCallsFragment().setOnSelectCallListener(actionListener);
    }

    public interface OnSelectCallListener {
        void onSelectCall(Call selected);
    }
}
