package com.plivo.voicecalling.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plivo.voicecalling.R;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DialerActivity extends AppCompatActivity{

    EditText phoneNumberText;
    Button callBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        phoneNumberText = (EditText)findViewById(R.id.editText3);

        callBtn = (Button) findViewById(R.id.callBtn);

        callBtn.setOnClickListener((View v) -> {

            // Do something
            if (phoneNumberText.getText().toString().equals("")) {

                Log.d("Invalid Data", "Please enter valid number");

            } else {

                addProd(phoneNumberText.getText().toString());

                Intent intent = new Intent(this, ActiveCall.class);
                intent.putExtra("data", phoneNumberText.getText().toString());
                startActivity(intent);
            }

        });
    }

    public String getDate(){

        DateFormat dfDate = new SimpleDateFormat("yyyy/MM/dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }

    public void addProd(String contactNum)
    {
        HashMap<String, String> prodHashMap = new HashMap<String, String>();
        prodHashMap.put("Phone", contactNum);
        prodHashMap.put("Time", getDate());

        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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

}
