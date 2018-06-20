package com.plivo.voicecalling.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plivo.voicecalling.R;
import com.plivo.voicecalling.Helpers.SelectUser;
import com.plivo.voicecalling.Helpers.SelectUserAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class PhoneContacts extends Fragment {

    private static final int CONTACTS_LOADER_ID = 1;

    // ArrayList
    ArrayList<SelectUser> selectUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load PhoneContacts list
    Cursor phones, email;

    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;

    /** (non-Javadoc)
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        //((MainActivity)getActivity()).setOnBackPressedListener(new BaseBackPressedListener(getActivity()));

        View rootView = inflater.inflate(R.layout.activity_contacts, container, false);

        listView = (ListView)rootView.findViewById(R.id.list);

        selectUsers = new ArrayList<SelectUser>();
        resolver = getActivity().getApplicationContext().getContentResolver();
        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        LoadContact loadContact = new LoadContact();
        loadContact.execute();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        return rootView;
    }

    public String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy/MM/dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }

    public void addProd(SelectUser selectUser)
    {
        HashMap<String, String> prodHashMap = new HashMap<String, String>();
        prodHashMap.put("Phone", selectUser.getPhone());
        prodHashMap.put("Time", getDate());

        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(getContext());

        Gson gson = new Gson();
        String arrayListString = db.getString("RecentInfo", null);
        Type type = new TypeToken<ArrayList>() {}.getType();
        ArrayList arrayList = gson.fromJson(arrayListString, type);

        if(arrayList == null)
        {
            arrayList = new ArrayList();
        }

        arrayList.add(prodHashMap);

        SharedPreferences.Editor collection = db.edit();
        Gson gson2 = new Gson();
        String arrayList1 = gson2.toJson(arrayList);

        collection.putString("RecentInfo", arrayList1);
        collection.commit();


        String arrayListString3 = db.getString("RecentInfo", null);
        Type type3 = new TypeToken<ArrayList>() {}.getType();
        ArrayList arrayList3 = gson.fromJson(arrayListString3, type3);

        Log.d("Saved data ", "SharedPreferences "+arrayList3);

    }

//    @Override
//    public void onBackButtonPressed() {
//
//        getActivity().onBackPressed();
//
//    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.d("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getActivity(), "No PhoneContacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectUser selectUser = new SelectUser();
                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUser.setCheckedBox(false);
                    selectUsers.add(selectUser);
                }

            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers, getActivity());
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectUser data = selectUsers.get(i);
                    addProd(data);

                    Intent intent = new Intent(getActivity(), ActiveCall.class);
                    intent.putExtra("data", data.getPhone());
                    getActivity().startActivity(intent);

                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        phones.close();
    }
}

