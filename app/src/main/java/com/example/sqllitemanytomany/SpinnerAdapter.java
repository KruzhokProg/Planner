package com.example.sqllitemanytomany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    List<Tag> tags;
    Context context;
    LayoutInflater inflater;

    public SpinnerAdapter(List<Tag> tags, Context context) {
        this.tags = tags;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int i) {
        return tags.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        return getCustomView(position,view);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getCustomView(i,view);
    }

    public View getCustomView(int i, View view)
    {
        view = inflater.inflate(R.layout.spinner_item,null);
        TextView tvLevel = view.findViewById(R.id.tvSpinnerItem);
        tvLevel.setText(tags.get(i).getName());
        return view;
    }
}
