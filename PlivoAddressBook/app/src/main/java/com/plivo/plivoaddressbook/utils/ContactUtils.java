package com.plivo.plivoaddressbook.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.Contact;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class ContactUtils {
    public static final String[] CONTACTS_COLUMNS = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
    };
    private Context context;

    public ContactUtils(Context context) {

        this.context = context;
    }

    public List<Call> getCallLog() {
        List<Call> calls = new ArrayList<>();
        Cursor c = getCallLogProvider();
        c.moveToFirst();
        do{
            String callerID = c.getString(c.getColumnIndex(CallLog.Calls._ID));
            String callerNumber = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
            long callDateandTime = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
            long callDuration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
            int callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));

            calls.add(new Call.Builder()
                    .setType(
                            callType == CallLog.Calls.INCOMING_TYPE ? Call.TYPE.INCOMING:
                                    callType == CallLog.Calls.OUTGOING_TYPE ? Call.TYPE.OUTGOING:
                                            Call.TYPE.MISSED)
                    .setCreatedAt(callDateandTime)
                    .setId(callerID)
                    .setDuration(callDuration)
                    .setContact(getContact(callerNumber))
                    .build());
        } while(c.moveToNext());
        c.close();

        return calls;
    }

    private Cursor getCallLogProvider() {
        if (checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        String[] projection = new String[] {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC");

        return c.getCount() > 0 ? c : null;
    }

    public Contact getContact(String phone_number) {
        Cursor c = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                CONTACTS_COLUMNS,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?",
                new String[]{phone_number},
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC");

        if (c.moveToNext()) {
            String displayName = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME_PRIMARY));
            String displayNumber = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
            String displayThumbnailUri = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
            c.close();
            return new Contact.Builder()
                    .setName(displayName)
                    .setPhoneNumber(displayNumber)
                    .setPhotoUri(Uri.parse(displayThumbnailUri))
                    .build();
        }

        return new Contact.Builder()
                .setName(phone_number)
                .setPhoneNumber(phone_number)
                .build();
    }

    public List<Contact> getAllContacts() {
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                CONTACTS_COLUMNS,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC");
        List<Contact> contactList = new ArrayList<>();
        while (c.moveToNext()) {
            String name=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new Contact.Builder()
                    .setName(name)
                    .setPhoneNumber(phoneNumber)
                    .build());
        }
        c.close();

        return contactList;
    }
}
