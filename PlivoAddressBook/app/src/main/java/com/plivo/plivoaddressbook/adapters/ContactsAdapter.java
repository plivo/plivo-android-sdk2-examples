package com.plivo.plivoaddressbook.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Contact;
import com.plivo.plivoaddressbook.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.RecyclerViewHolder> implements Filterable {

    private List<Contact> items;
    private OnItemClickListener itemClickListener;

    private ContactsFilter filter = new ContactsFilter();

    public ContactsAdapter(List<Contact> items) {
        this.items = items;
    }

    public void setItems(List<Contact> contacts) {
        this.items = contacts;
        this.filter.setContacts(contacts);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_contact, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Contact item = items.get(position);

        holder.contactNameTextView.setText(item.getName());
        holder.phoneNumberTextView.setText(item.getPhoneNumber());

        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.name)
        AppCompatTextView contactNameTextView;

        @BindView(R.id.phone_number)
        AppCompatTextView phoneNumberTextView;

        private Contact data;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(this.data);
            }
        }

        public void setData(Contact contact) {
            this.data = contact;
        }
    }

    private class ContactsFilter extends Filter {

        private List<Contact> allContacts = new ArrayList<>();

        private void setContacts(List<Contact> contacts) {
            allContacts.addAll(contacts);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                results.count = allContacts.size();
                results.values = allContacts;
            } else {
                ArrayList<Contact> filteredItems = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (Contact c : allContacts) {
                    if (c.getName().toLowerCase().contains(constraint) ||
                            c.getPhoneNumber().contains(constraint)) {
                        filteredItems.add(c);
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            setItems((List<Contact>) results.values);
            notifyDataSetChanged();
        }
    }
}
