package com.example.sqllitemanytomany;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatisticAdapter extends BaseAdapter {

    Context context;
    Integer Done, All;
    LayoutInflater inflater;

    public StatisticAdapter(Context context, Integer Done, Integer All) {
        this.context = context;
        this.Done = Done;
        this.All = All;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_statistic, null);
        TextView tvTaskDone = view.findViewById(R.id.tvDone);
        TextView tvTaskAll = view.findViewById(R.id.tvAll);
        tvTaskDone.setText(Done.toString());
        tvTaskAll.setText(All.toString());
        return view;
    }
}
