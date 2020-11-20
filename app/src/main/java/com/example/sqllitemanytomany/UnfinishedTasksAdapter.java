package com.example.sqllitemanytomany;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UnfinishedTasksAdapter extends BaseAdapter {

    List<Task> lstUnfinishedTasks;
    List<Tag> lstTag;
    Context context;
    LayoutInflater inflater;
    private SparseBooleanArray mCheckedMap = new SparseBooleanArray();

    public UnfinishedTasksAdapter(List<Task> lstUnfinishedTasks, List<Tag> lstTag,  Context context) {
        this.lstUnfinishedTasks = lstUnfinishedTasks;
        this.lstTag = lstTag;
        this.context = context;
        inflater = LayoutInflater.from(context);

        for (int i=0; i<lstUnfinishedTasks.size(); i++)
            mCheckedMap.put(i,false);
    }

    void toggleChecked(int position) {
        if(mCheckedMap.get(position)){
            mCheckedMap.put(position, false);
        } else{
            mCheckedMap.put(position, true);
        }
        notifyDataSetChanged();
    }

    public List<Task> getCheckedItems()
    {
        List<Task> checkedItems = new ArrayList<>();

        for (int i = 0; i < mCheckedMap.size(); i++) {
            if (mCheckedMap.get(i)) {
                checkedItems.add(lstUnfinishedTasks.get(i));
            }
        }
        return checkedItems;
    }

    @Override
    public int getCount() {
        return lstUnfinishedTasks.size();
    }

    @Override
    public Object getItem(int i) {
        return lstUnfinishedTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder
    {
        CheckedTextView tvUnfinishedTask;
        TextView tvUnfinishedLevel;
        //CheckBox cbUnfinished;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view==null) {
            view = inflater.inflate(R.layout.item_unfinished_taks,null);
            viewHolder = new ViewHolder();
            viewHolder.tvUnfinishedTask = view.findViewById(R.id.tvUnfinishedTask);
            viewHolder.tvUnfinishedLevel = view.findViewById(R.id.tvUnfinishedLevel);
            //viewHolder.cbUnfinished = view.findViewById(R.id.cbUnfinished);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvUnfinishedTask.setText(lstUnfinishedTasks.get(i).getName());
        viewHolder.tvUnfinishedLevel.setText(lstTag.get(lstUnfinishedTasks.get(i).getTagId()-1).getName());
        Boolean checked = mCheckedMap.get(i);
        if (checked != null) {
            viewHolder.tvUnfinishedTask.setChecked(checked);
        }
//        viewHolder.cbUnfinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                Toast.makeText(context, "Выбрали элемент: " + lstUnfinishedTasks.get(i).getName(), Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }
}
