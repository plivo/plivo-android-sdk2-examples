package com.plivo.plivoaddressbook;

import android.os.Bundle;
import android.view.View;

import com.plivo.plivoaddressbook.model.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    private String name;
    private OnFragmentLoadedObserver fragmentLoadedObserver;

    public BaseFragment() {
        setName(this.getClass().getSimpleName());
    }

    public String getName() {
        return name;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fragmentLoadedObserver != null) {
            fragmentLoadedObserver.onFragmentLoaded();
        }
    }

    protected BaseFragment setName(String fragmentName) {
        this.name = fragmentName;
        return this;
    }

    protected void showState(Call.STATE state) {
        if (getActivity() != null && state != null) {
            getActivity().setTitle("Call " + state.name());
        }
    }

    protected void removeFragment() {
        getChildFragmentManager().beginTransaction()
                .remove(this)
                .commitAllowingStateLoss();
    }

    public void updateUi(Call call) {
    }

    public BaseFragment setFragmentLoadedObserver(OnFragmentLoadedObserver fragmentLoadedObserver) {
        this.fragmentLoadedObserver = fragmentLoadedObserver;
        return this;
    }

    public interface OnFragmentLoadedObserver {
        void onFragmentLoaded();
    }
}
