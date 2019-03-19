package com.plivo.plivoincomingcall.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private String id;
    private String name;
    private String phoneNumber;
    private Uri photoUri;

    private Contact(String id, String name, String number, Uri photoUri) {
        this.id = id;
        this.name = name;
        this.phoneNumber = number;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public static class Builder {
        private String id;
        private String name;
        private String phoneNumber;
        private Uri photoUri;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Contact build() {
            return new Contact(this.id, this.name, this.phoneNumber, this.photoUri);
        }

    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        photoUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeParcelable(photoUri, flags);
    }
}
