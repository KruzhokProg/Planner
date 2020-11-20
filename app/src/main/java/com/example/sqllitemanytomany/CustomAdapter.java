package com.example.sqllitemanytomany;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    List<Task> lstToDo;
    List<Tag> lstTag;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(List<Task> lstToDo, List<Tag> lstTag, Context context) {
        this.lstToDo = lstToDo;
        this.lstTag = lstTag;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lstToDo.size();
    }

    @Override
    public Object getItem(int i) {
        return lstToDo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder
    {
        TextView tvToDoDesc;
        TextView tvToDoLevel;
        ImageView imgAttach;
        LinearLayout llItem;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view==null) {
            view = inflater.inflate(R.layout.item_todo_list,null);
            viewHolder = new ViewHolder();
            viewHolder.tvToDoDesc = view.findViewById(R.id.tvToDo);
            viewHolder.tvToDoLevel = view.findViewById(R.id.tvToDoLevel);
            viewHolder.llItem = view.findViewById(R.id.llItem);
            viewHolder.imgAttach = view.findViewById(R.id.imgAttach);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.tvToDoDesc.setText(lstToDo.get(i).getName());
        viewHolder.tvToDoLevel.setText(lstTag.get(lstToDo.get(i).getTagId()-1).getName());
        if(lstToDo.get(i).getFinished().equals("true"))
            viewHolder.llItem.setBackgroundColor(Color.rgb(201,192,187));
        if(!lstToDo.get(i).getImagePath().equals(""))
            viewHolder.imgAttach.setVisibility(View.VISIBLE);
        else
            viewHolder.imgAttach.setVisibility(View.INVISIBLE);
//        view = inflater.inflate(R.layout.item_todo_list,null);
//        TextView tvToDoDesc = view.findViewById(R.id.tvToDo);
//        TextView tvToDoLevel = view.findViewById(R.id.tvToDoLevel);
//        tvToDoDesc.setText(lstToDo.get(i).getName());
//        tvToDoLevel.setText(lstTag.get(lstToDo.get(i).getTagId()-1).getName());
//        LinearLayout llItem = view.findViewById(R.id.llItem);
//
//        if (lstToDo.get(i).getFinished().equals("true"))
//            llItem.setBackgroundColor(Color.rgb(201,192,187));
//
        return view;
    }
}
