package com.plivo.plivoaddressbook.screens.dial.tabs.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.adapters.ContactsAdapter;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.Contact;
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

public class ContactsFragment extends TabFragment implements ContactsAdapter.OnItemClickListener {

    @BindView(R.id.contacts_recyclerView)
    RecyclerView recyclerView;

    @Inject
    ContactsAdapter contactsAdapter;

    private ContactViewModel viewModel;

    private OnClickContactListener onClickContactListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        ((DialActivity) getActivity()).getViewComponent().inject(this);
    }

    @Override
    public String getTitle() {
        return "Contacts";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        contactsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(contactsAdapter);

        viewModel.contactsObserver().observe(this, contacts -> {
            contactsAdapter.setItems(contacts);
        });
    }

    @Override
    public void updateUi(Call call) {
        super.updateUi(call);
        viewModel.getContactsList();
    }

    @Override
    public void onItemClick(Contact selected) {
        if (onClickContactListener != null) onClickContactListener.onClickContact(selected);
    }

    public void filterContacts(String text) {
        contactsAdapter.getFilter().filter(text);
    }

    private ContactsFragment setOnClickContactListener(OnClickContactListener onClickContactListener) {
        this.onClickContactListener = onClickContactListener;
        return this;
    }

    public static ContactsFragment newInstance(OnClickContactListener actionListener) {
        return new ContactsFragment().setOnClickContactListener(actionListener);
    }

    public interface OnClickContactListener {
        void onClickContact(Contact selected);
    }
}
