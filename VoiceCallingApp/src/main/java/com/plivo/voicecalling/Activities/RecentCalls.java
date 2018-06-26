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
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.plivo.voicecalling.R;
import com.plivo.voicecalling.Helpers.RecentCall;
import com.plivo.voicecalling.Helpers.RecentCallAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentCalls extends Fragment {

    RecentCallAdapter adapter;
    ArrayList<RecentCall> recentCalls;

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

        View rootView = inflater.inflate(R.layout.activity_recent_calls, container, false);

        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String arrayListString3 = db.getString("RecentInfo", null);
        List<Map<String, Object>> parsedList = gson.fromJson(arrayListString3, List.class);

        recentCalls = new ArrayList<RecentCall>();

        if(parsedList != null) {

            Collections.reverse(parsedList);

            for (Map<String, Object> parsedItem : parsedList) {

                RecentCall recent = new RecentCall();
                recent.setPhone(parsedItem.get("Phone").toString());
                recent.setTime(parsedItem.get("Time").toString());
                recentCalls.add(recent);

            }

        }

        ListView listView = (ListView)rootView.findViewById(R.id.list);
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

        return rootView;

    }

    public String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy/MM/dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }

    public void addProd(RecentCall recent)
    {
        HashMap<String, String> prodHashMap = new HashMap<String, String>();
        prodHashMap.put("Phone", recent.getPhone());
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
//    @Override
//    public void onBackButtonPressed() {
//
//            getActivity().onBackPressed();
//
//    }

    class MyTypeAdapter<T> extends TypeAdapter<T> {
        public T read(JsonReader reader) throws IOException {
            return null;
        }

        public void write(JsonWriter writer, T obj) throws IOException {
            if (obj == null) {
                writer.nullValue();
                return;
            }
            writer.value(obj.toString());
        }
    }

}
