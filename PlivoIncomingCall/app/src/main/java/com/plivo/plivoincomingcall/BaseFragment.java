package com.plivo.plivoincomingcall;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    private String name;

    public BaseFragment() {
        setName(this.getClass().getSimpleName());
    }

    public String getName() {
        return name;
    }

    protected BaseFragment setName(String fragmentName) {
        this.name = fragmentName;
        return this;
    }

    protected void setTitle(String title) {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    public void updateUi() {
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }
}
