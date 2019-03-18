package com.plivo.plivoincomingcall.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class Call implements Parcelable {
    private static final String TAG = Call.class.getSimpleName();

    public static final long CALL_RINGING_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public enum TYPE {
        INCOMING,
        OUTGOING,
        MISSED
    }

    public enum STATE {
        IDLE,
        RINGING, // ringing after call is outgoing/incoming
        ANSWERED, // outgoing/incoming call is answered
        HANGUP,
        REJECTED,
        INVALID // made a out call to invalid phone number
    }

    private String id="";
    private Contact contact;
    private Object data;
    private long createdAt;
    private long duration;
    private boolean isMute;
    private boolean isActive;
    private boolean isHold;
    private int tick;

    private STATE state;
    private TYPE type;

    private Call(String id, Contact contact, STATE state, TYPE type, Object data, long createdAt, long duration) {
        this.id = id;
        this.contact = contact;
        this.state = state;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public Contact getContact() {
        return contact;
    }

    public STATE getState() {
        return state;
    }

    public long getDuration() {
        return duration;
    }

    public void setState(STATE state) {
        this.state = state;
        setActive(isAnswered());
    }

    // handy statics
    public static Call newCall(String phone_num) {
        return new Builder()
                .setContact(new Contact.Builder().setPhoneNumber(phone_num).build())
                .build();
    }

    public static Call newCall(Contact contact) {
        return new Builder()
                .setContact(contact)
                .build();
    }

    public TYPE getType() {
        return type;
    }

    public boolean isIncoming() {
        return type == TYPE.INCOMING;
    }

    public boolean isOutgoing() {
        return type == TYPE.OUTGOING;
    }

    public Object getData() {
        return data;
    }

    public boolean isRinging() { return state == STATE.RINGING; }
    public boolean isHangedUp() { return state == STATE.HANGUP; }
    public boolean isAnswered() { return state == STATE.ANSWERED; }
    public boolean isIdle() { return state == STATE.IDLE; }
    public boolean isInvalid() { return state == STATE.INVALID; }
    public boolean isRejected() { return state == STATE.REJECTED; }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return isRinging() &&
                System.currentTimeMillis() - getCreatedAt() > CALL_RINGING_TIMEOUT;
    }

    public boolean isMute() {
        Log.d(TAG, contact.getPhoneNumber() + " isMute " + isMute);
        return isMute;
    }

    public void setMute(boolean mute) {
        Log.d(TAG, contact.getPhoneNumber() + " setMute " + mute);
        isMute = mute;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isHold() {
        return isHold;
    }

    public void setHold(boolean hold) {
        isHold = hold;
    }

    public static class Builder {
        private String id;
        private Contact contact;

        private long createdAt;
        private long duration;


        private STATE state;
        private TYPE type;

        private Object data;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setState(STATE state) {
            this.state = state;
            return this;
        }

        public Builder setType(TYPE type) {
            this.type = type;
            return this;
        }

        public Builder setContact(Contact contact) {
            this.contact = contact;
            return this;
        }

        // like incoming/outgoing obj from SDK
        public Builder setData(Object data) {
            this.data = data;
            setCreatedAt(System.currentTimeMillis());
            return this;
        }

        public Builder setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Call build() {
            return new Call(this.id,
                    contact,
                    state,
                    type,
                    data,
                    createdAt,
                    duration);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(createdAt);
        dest.writeLong(duration);
        dest.writeByte((byte) (isMute ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(state.name());
        dest.writeString(type.name());
//        dest.writeValue(data);
        dest.writeParcelable(contact, flags);
    }

    protected Call(Parcel in) {
        id = in.readString();
        createdAt = in.readLong();
        duration = in.readLong();
        isMute = in.readByte() != 0;
        isActive = in.readByte() != 0;
        state = STATE.valueOf(in.readString());
        type = TYPE.valueOf(in.readString());
//        data = in.readValue(Object.class.getClassLoader());
        contact = in.readParcelable(Contact.class.getClassLoader());
    }

    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };
}
