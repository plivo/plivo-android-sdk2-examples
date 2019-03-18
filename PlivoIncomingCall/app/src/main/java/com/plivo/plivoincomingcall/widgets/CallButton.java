package com.plivo.plivoincomingcall.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.model.Call;

import androidx.appcompat.widget.AppCompatButton;

public class CallButton extends AppCompatButton {

    private static final String TAG = CallButton.class.getSimpleName();
    private Call.STATE state;

    private boolean isStateChanged;

    public CallButton(Context context) {
        this(context, null);
    }

    public CallButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CallState, 0, 0);
        state = Call.STATE.values()[a.getInt(R.styleable.CallState_state_call, 0)];
        a.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        /*if (state == null || isStateChanged) {
            final int[] drawableStates = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableStates, new int[]{R.attr.state_call});
            return drawableStates;
        } else {
            return super.onCreateDrawableState(extraSpace);
        }*/
        return super.onCreateDrawableState(extraSpace);
    }

    public void setState(Call.STATE state) {
        isStateChanged = this.state != state;
        Log.d(TAG, "setstate:" + state + ", isStateChanged:" + isStateChanged);
        this.state = state;
        refreshDrawableState();
    }

    public Call.STATE getState() {
        Log.d(TAG, "getState " + state);
        return state;
    }
}
