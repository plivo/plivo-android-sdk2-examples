package com.plivo.voicecalling.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plivo.voicecalling.R;
import com.plivo.voicecalling.Helpers.RecentCall;
import com.plivo.voicecalling.Helpers.RecentCallAdapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SipContacts extends Fragment {

    SharedPreferences sharedPreferences;
    RecentCallAdapter adapter;
    ArrayList<RecentCall> recentCalls;

    /**
     * (non-Javadoc)
     *
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Retrieve the values
        Set<String> sipSet = sharedPreferences.getStringSet("SIP Contacts", null);
        List<String> list = new ArrayList<String>(sipSet);

        recentCalls = new ArrayList<RecentCall>();

        try
        {

            Map<String, String> retMap = new Gson().fromJson(
                    list.get(0), new TypeToken<HashMap<String, String>>() {}.getType()
            );

            ArrayList<String> valuesList = new ArrayList<String>(retMap.values());
            ArrayList<String> keysList = new ArrayList<String>(retMap.keySet());

            for(int i = 0; i < valuesList.size(); i ++){

                if(isValidEmail(keysList.get(i))) {

                    RecentCall recent = new RecentCall();
                    recent.setPhone(valuesList.get(i));
                    recent.setTime(keysList.get(i));
                    recentCalls.add(recent);
                }

            }

        }
        catch (Exception e) {
            // Do something with the exception
            Log.d("Exception",e.getLocalizedMessage());
        }


        View view = inflater.inflate(R.layout.activity_sip_contacts, container, false);

        ListView listView = (ListView)view.findViewById(R.id.list);
        adapter = new RecentCallAdapter(recentCalls, getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                RecentCall data = recentCalls.get(i);

                addProd(data);

                Intent intent = new Intent(getActivity(), ActiveCall.class);
                intent.putExtra("data", data.getPhone());
                getActivity().startActivity(intent);

            }
        });

        listView.setFastScrollEnabled(true);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        return view;
    }

    public void addProd(RecentCall recent)
    {
        HashMap<String, String> prodHashMap = new HashMap<String, String>();
        prodHashMap.put("Phone", "iOSApp170426075413");
        prodHashMap.put("Time", getDate());

        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(getContext());

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

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy/MM/dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }
}
