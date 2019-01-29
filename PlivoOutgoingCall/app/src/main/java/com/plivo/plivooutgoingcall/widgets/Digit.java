package com.plivo.plivooutgoingcall.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.plivo.plivooutgoingcall.R;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class Digit extends AppCompatButton {
    public Digit(Context context) {
        this(context, null);
    }

    public Digit(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Digit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.dial_pad, 0, 0);
        String digit = a.getString(R.styleable.dial_pad_digit);

        setText(digit);
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));

    }
}
