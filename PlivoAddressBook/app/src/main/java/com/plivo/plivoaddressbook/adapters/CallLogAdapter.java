package com.plivo.plivoaddressbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.Contact;
import com.plivo.plivoaddressbook.utils.DateUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.RecyclerViewHolder> {

    private List<Call> items;
    private OnItemClickListener itemClickListener;

    private DateUtils dateUtils;

    public CallLogAdapter(List<Call> items, DateUtils dateUtils) {
        this.items = items;
        this.dateUtils = dateUtils;
    }

    public void setItems(List<Call> calls) {
        this.items = calls;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_call_log, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Call item = items.get(position);

        Contact contact = item.getContact();
        if (contact != null) {
            holder.contactImageView.setImageURI(contact.getPhotoUri());
            holder.contactTitleTextView.setText(contact.getName());
            holder.usernameTextView.setText(contact.getPhoneNumber());
        }
        holder.whenTextView.setText(dateUtils.prettyRelativeDate(item.getCreatedAt()));
        holder.statusImageView.setImageDrawable(ContextCompat.getDrawable(holder.statusImageView.getContext(),
                item.isIncoming() ? android.R.drawable.sym_call_incoming:
                        item.isOutgoing() ? android.R.drawable.sym_call_outgoing:
                                android.R.drawable.sym_call_missed
        ));

        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Call item);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.contact_image)
        AppCompatImageView contactImageView;

        @BindView(R.id.contact_title)
        AppCompatTextView contactTitleTextView;

        @BindView(R.id.username)
        AppCompatTextView usernameTextView;

        @BindView(R.id.status)
        AppCompatImageView statusImageView;

        @BindView(R.id.when)
        AppCompatTextView whenTextView;

        @BindView(R.id.call_btn)
        AppCompatImageButton callButton;

        private Call data;

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

        public void setData(Call data) {
            this.data = data;
        }
    }
}
