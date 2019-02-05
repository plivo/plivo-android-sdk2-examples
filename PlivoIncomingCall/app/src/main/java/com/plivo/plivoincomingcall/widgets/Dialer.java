package com.plivo.plivoincomingcall.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.plivo.plivoincomingcall.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dialer extends FrameLayout {
    @BindView(R.id.number_editText)
    AppCompatEditText numberEditText;

    @BindView(R.id.num_pad)
    GridLayout numPad;

    private OnDigitClickListener digitListener;

    public Dialer(Context context) {
        this(context, null);
    }

    public Dialer(Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dialer(Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialer, null);
        addView(v);
        ButterKnife.bind(this);

        int position = -1;
        while (position++ < numPad.getChildCount()-1) {
            numPad.getChildAt(position).setOnClickListener(digitOnClickListener);
        }
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
