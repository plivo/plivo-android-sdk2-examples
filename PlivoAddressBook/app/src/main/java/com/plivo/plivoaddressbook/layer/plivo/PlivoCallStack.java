package com.plivo.plivoaddressbook.layer.plivo;

import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlivoCallStack {

    private List<Call> callStack = new ArrayList<>();

    public Call getCurrentCall() {
        return callStack.isEmpty() ? null : callStack.get(0); // first one is the current
    }

    public void setCurrentCall(Call call) {
        if (call == null || callStack.isEmpty() || !callStack.contains(call)) return;

        Collections.swap(callStack, 0, callStack.indexOf(call));
    }

    public Call getCall(String callId) {
        if (callId == null || callStack.isEmpty()) return null;

        for (Call call : callStack) {
            if (call.getId() == callId ) return call;
        }

        return null;
    }

    public List<Call> getCallStack() {
        return callStack;
    }

    public boolean removeFromCallStack(Call call) {
        if (call == null || callStack.isEmpty() || !callStack.contains(call)) return false;

        return callStack.remove(call);
    }

    public void clearCallStack() {
        callStack.clear();
    }

    public boolean addToCallStack(Call call) {
        if (call == null || callStack.contains(call)) return false;

        callStack.add(0, call);

        return true;
    }
}
