package com.plivo.plivoincomingcall.utils;

import android.os.CountDownTimer;

import com.plivo.plivoincomingcall.model.Call;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for holding Call Timer tick.
 */
public class TickManager {
    private static final long PERIOD = TimeUnit.SECONDS.toMillis(1);
    private static final long MAX = TimeUnit.HOURS.toMillis(24);
    private static final String TAG = TickManager.class.getSimpleName();

    private HashMap<String, Integer> callTickMap = new HashMap<>(); // <call_id, tick_value>
    private CountDownTimer timer;
    private OnTickListener tickListener;
    private int tick;

    public void start(Call call) {
        if (call == null) return;

        // keeping null check to avoid multiple start()
        if (timer == null) {
            timer = new CountDownTimer(MAX, PERIOD) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tick++;
                    call.setTick(tick);
                    if (tickListener != null) {
                        tickListener.onTick(tick);
                    }
                }

                @Override
                public void onFinish() {
                    call.setTick(0);
                }
            };

            // use while pause timer if any such case
//            tick = getTick(call);
            tick = call.getTick();
            timer.start();
        }
    }

    public void stop(Call call) {
        if (call == null) return;

        call.setTick(0);
        cancelTimer();
        callTickMap.remove(call.getId());
    }

    public void pause(Call call) {
        if (call == null) return;

        // currently no timer pause
//        cancelTimer();
//        callTickMap.put(call.getId(), tick);
    }

    public int getTick(Call call) {
        if (callTickMap.containsKey(call.getId())) {
            return callTickMap.get(call.getId());
        }
        return 0;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setTickListener(OnTickListener tickListener) {
        this.tickListener = tickListener;
    }

    public interface OnTickListener {
        void onTick(int tick);
    }

}
