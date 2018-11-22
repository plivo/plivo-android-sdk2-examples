package com.plivo.plivoaddressbook.adapters;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.utils.DateUtils;
import com.plivo.plivoaddressbook.utils.TickManager;
import com.plivo.plivoaddressbook.widgets.CallTimer;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreCallsAdapter extends RecyclerView.Adapter<MoreCallsAdapter.RecyclerViewHolder> {

    private List<Call> items;
    private OnItemClickListener itemClickListener;

    private DateUtils dateUtils;

    private int screenWidth;
    private int screenHeight;
    private TickManager tickManager;

    public MoreCallsAdapter(List<Call> items, DateUtils dateUtils,
                            DisplayMetrics displayMetrics,
                            TickManager tickManager) {
        this.items = items;
        this.dateUtils = dateUtils;
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;
        this.tickManager = tickManager;
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
        View itemView = inflater.inflate(R.layout.list_item_more_calls, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Call item = items.get(position);

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params != null) {
            params.width = getItemCount() > 1 ? screenWidth/2:
                            screenWidth;
        }
        holder.callTypeTextView.setText(item.getType().name());
        holder.callStateTextView.setText(item.getState().name());
        holder.contactName.setText(item.getContact().getName());
        holder.contactNumber.setText(item.getContact().getPhoneNumber());
        holder.contactCircleAlpha.setText(item.getContact().getName().substring(0,1));
        holder.atTextView.setText(dateUtils.prettyRelativeDate(item.getCreatedAt()));
        holder.callTimerDisplay.setTick(tickManager.getTick(item));

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
        @BindView(R.id.call_type)
        AppCompatTextView callTypeTextView;

        @BindView(R.id.call_state)
        AppCompatTextView callStateTextView;

        @BindView(R.id.duration)
        CallTimer callTimerDisplay;

        @BindView(R.id.contact_circle)
        AppCompatTextView contactCircleAlpha;

        @BindView(R.id.contact_name)
        AppCompatTextView contactName;

        @BindView(R.id.contact_number)
        AppCompatTextView contactNumber;

        @BindView(R.id.at)
        AppCompatTextView atTextView;

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
