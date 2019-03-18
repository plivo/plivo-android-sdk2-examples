package com.plivo.plivoincomingcall.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.utils.TickManager;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

public class CallTimer extends AppCompatTextView implements TickManager.OnTickListener {
    private static final String TAG = CallTimer.class.getSimpleName();
    private static final String HH_MM_SS = "%02d:%02d:%02d";
    private static final String MM_SS = "%02d:%02d";

    public CallTimer(Context context) {
        this(context, null);
    }

    public CallTimer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setTick(int tick) {
        updateText(tick);
    }

    private void init(AttributeSet attrs) {
        setText(String.format(MM_SS, 0, 0));
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.timer_size));
    }

    private void updateText(int tick) {
        int hours = (int) TimeUnit.SECONDS.toHours(tick);
        int minutes = (int) TimeUnit.SECONDS.toMinutes(tick-=TimeUnit.HOURS.toSeconds(hours));
        int seconds = (int) (tick-TimeUnit.MINUTES.toSeconds(minutes));
        if (hours > 0) {
            setText(String.format(HH_MM_SS, hours, minutes, seconds));
        } else {
            setText(String.format(MM_SS, minutes, seconds));
        }
    }

    @Override
    public void onTick(int tick) {
        setTick(tick);
    }
}
