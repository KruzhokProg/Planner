package com.example.sqllitemanytomany;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    List<Task> tasks, filteredTasks;
    Context context;
    LayoutInflater inflater;

    public AutoCompleteAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredTasks.size();
    }

    @Override
    public String getItem(int i) {
        return filteredTasks.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.auto_complete_item,null);
        TextView tvTask = view.findViewById(R.id.tvAutoCompleteItem);
        tvTask.setText(filteredTasks.get(i).getName());
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence!=null) {
                    filteredTasks = new ArrayList<>();
                    for (int i = 0; i < tasks.size(); i++) {
                        if (tasks.get(i).getName().startsWith(charSequence.toString()))
                            filteredTasks.add(tasks.get(i));
                    }
                    filterResults.values = filteredTasks;
                    filterResults.count = filteredTasks.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
