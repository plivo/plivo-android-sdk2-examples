package com.plivo.voicecalling.Helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.plivo.voicecalling.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentCallAdapter extends BaseAdapter {

    public List<RecentCall> _data;
    private ArrayList<RecentCall> arraylist;
    Context _c;
    ViewHolder v;

    public RecentCallAdapter(List<RecentCall> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<RecentCall>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_list, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();

        v.phone = (TextView) view.findViewById(R.id.name);
        v.time = (TextView) view.findViewById(R.id.no);

        final RecentCall data = (RecentCall) _data.get(i);
        v.phone.setText(data.getPhone());
        v.time.setText(data.getTime());

        view.setTag(data);
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (RecentCall wp : arraylist) {
                if (wp.getPhone().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        TextView phone, time;
    }
}
