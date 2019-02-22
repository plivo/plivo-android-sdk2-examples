package com.plivo.plivoaddressbook.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.plivo.plivoaddressbook.R;
import com.plivo.plivoaddressbook.model.Call;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dialer extends FrameLayout {
    private static final String[] KEYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};

    @BindView(R.id.number_editText)
    AppCompatEditText numberEditText;

    @BindView(R.id.num_pad)
    GridView numPad;

    private OnDigitClickListener digitListener;

    public Dialer(Context context) {
        this(context, null);
    }

    public Dialer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dialer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialer, null);
        addView(v);
        ButterKnife.bind(this);

        numPad.setAdapter(new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return KEYS.length;
            }

            @Override
            public String getItem(int position) {
                return KEYS[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.digit, parent, false);
                ((AppCompatButton) v).setText(KEYS[position]);
                v.setOnClickListener(digitOnClickListener);
                return v;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
    }

    public void setOnDigitClickListener(OnDigitClickListener listener) {
        this.digitListener = listener;
    }

    @OnClick(R.id.delete_btn)
    void onClickDelete() {
        String value = numberEditText.getText().toString();
        if (value.length() < 1) return;
        setText(value.substring(0, value.length()-1));
    }

    public String getText() {
        return numberEditText.getText().toString();
    }

    private void setText(String value) {
        numberEditText.setText(value);
        numberEditText.setSelection(value.length());
    }

    private OnClickListener digitOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String value = numberEditText.getText().toString();
            String digit = ((AppCompatButton) v).getText().toString();
            setText(value + digit);
            if (digitListener != null) {
                digitListener.onClickDigit(digit);
            }
        }
    };

    public interface OnDigitClickListener {
        void onClickDigit(String digit);
    }

}
