package com.example.sqllitemanytomany;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatePicker dp;
    SwipeMenuListView lstv_tasks, lstv_head;
    DataBaseHelper db;
    List<Task> tasks;
    TextView tvEmptyList;
    String currentDate, selectedDate;
    List<Tag> tags;
    SpinnerAdapter spinnerAdapter;
    Integer countAllTasks, countFinisehdTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEmptyList = findViewById(R.id.tvEmptyList);
        lstv_tasks = findViewById(R.id.toDoList);
        lstv_head = findViewById(R.id.lstvHead);
        db = new DataBaseHelper(getApplicationContext());

        dp = findViewById(R.id.dp);
        currentDate = dp.getDayOfMonth() + "/" + getCorrectMonth(dp.getMonth()) + "/" + dp.getYear();

        UpdateTaskList(currentDate);

        dp.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                selectedDate = day + "/" + getCorrectMonth(month) + "/" + year;
                UpdateTaskList(selectedDate);
            }
        });

        // меню статистика и график
        SwipeMenuCreator creatorHeader = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem graphItem = new SwipeMenuItem(getApplicationContext());
                graphItem.setBackground(new ColorDrawable(Color.rgb(255,204,0)));
                graphItem.setWidth(200);
                graphItem.setIcon(R.drawable.ic_timeline);
                menu.addMenuItem(graphItem);
//
                SwipeMenuItem unfinishedTaskItem = new SwipeMenuItem(getApplicationContext());
                unfinishedTaskItem.setBackground(new ColorDrawable(Color.rgb(200,204,0)));
                unfinishedTaskItem.setWidth(200);
                unfinishedTaskItem.setIcon(R.drawable.ic_unfinished_task);
                menu.addMenuItem(unfinishedTaskItem);
            }
        };
        lstv_head.setMenuCreator(creatorHeader);
        lstv_head.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index)
                {
                    case 0:
                        // диалоговое окно графика
                        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                        View promptUserView = layoutInflater.inflate(R.layout.dialog_graph,null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setView(promptUserView);

                        // заполнение данными (из базы)
                        List<Statistic> statistics = db.getGrapthData();
                        List<BarEntry> values = new ArrayList<BarEntry>();

                        for (Statistic item : statistics) {
                            values.add(new BarEntry(item.getDay(), item.getPercent()));
                        }

                        // заполнение данными(тестовые)
//                        List<BarEntry> values = new ArrayList<BarEntry>();
//                        values.add(new BarEntry(2010, 80));
//                        values.add(new BarEntry(2011, 400));
//                        values.add(new BarEntry(2012, 300));
//                        values.add(new BarEntry(2013, 648));
//                        values.add(new BarEntry(2014, 312));
//                        values.add(new BarEntry(2015, 700));

                        BarChart barChart = promptUserView.findViewById(R.id.barChart);
                        BarDataSet barDataSet = new BarDataSet(values, "Процент выполнения");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(8f);


                        BarData barData = new BarData(barDataSet);
                        barChart.setFitBars(true);
                        barChart.setData(barData);
                        barChart.getDescription().setText("");
                        barChart.setScaleEnabled(true);
                        barChart.animateY(2000);

                        alertDialogBuilder.setTitle("Статистика выполнения задач за " + getMonthName(dp.getMonth()+1));
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                    case 1:
                        LayoutInflater layoutInflater2 = LayoutInflater.from(MainActivity.this);
                        final View unfinishedTasksView = layoutInflater2.inflate(R.layout.dialog_unfinished_tasks,null);
                        AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder2.setView(unfinishedTasksView);
                        final ListView lstvUnfinishedTask =  unfinishedTasksView.findViewById(R.id.lstvUnfinishedTasks);
                        // Здесь надо будет сделать spinner Tag'ов
                        alertDialogBuilder2.setTitle("Невыполненные задачи");
                        // Вытаскиваем из базы все невыполненные задачи
                        List<Task> unfinishedTasks = db.getAllUnfinishedTasks();
                        List<Tag> tags = db.getAllTags("");
                        final UnfinishedTasksAdapter adapter = new UnfinishedTasksAdapter(unfinishedTasks, tags, getBaseContext());
                        lstvUnfinishedTask.setAdapter(adapter);
                        lstvUnfinishedTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                adapter.toggleChecked(position);
                            }
                        });
                        alertDialogBuilder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
//                                String result = "";
                                List<Task> selectedTasks = adapter.getCheckedItems();
                                //String selectedDate = dp.getDayOfMonth() + "/" + getCorrectMonth(dp.getMonth()) + "/" + dp.getYear();
                                for (Task task:selectedTasks) {
                                    task.setCreateAt(selectedDate);
                                    task.setFinished("false");
                                    task.setImagePath("");
                                    db.createTask(task);
                                }
                                UpdateTaskList(selectedDate);
                                Toast.makeText(MainActivity.this, "Задачи успешно добавлены!", Toast.LENGTH_SHORT).show();
//                                for (int i = 0; i < resultList.size(); i++) {
//                                    result += String.valueOf(resultList.get(i)) + "\n";
//                                }
//
//                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
//                                        .show();
                            }
                        });

                        alertDialogBuilder2.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alertDialog2 = alertDialogBuilder2.create();
                        alertDialog2.show();
                }
                return false;
            }
        });


        // сдвигающееся меню
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem doneItem = new SwipeMenuItem(getApplicationContext());
                doneItem.setBackground(new ColorDrawable(Color.rgb(10,200,10)));
                doneItem.setWidth(200);
                doneItem.setIcon(R.drawable.ic_done);
                menu.addMenuItem(doneItem);

                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(255,204,0)));
                editItem.setWidth(200);
                editItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(200,0,0)));
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);

            }
        };
        lstv_tasks.setMenuCreator(creator);
        lstv_tasks.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index)
                {
                    case 0:
                        // done
                        Toast.makeText(MainActivity.this, tasks.get(position).getName(), Toast.LENGTH_SHORT).show();
                        Task task =  tasks.get(position);
                        if (task.getFinished().equals("false"))
                            task.setFinished("true");
                        else if(task.getFinished().equals("true"))
                            task.setFinished("false");
                        db.updateTask(task);
                        UpdateTaskList(selectedDate);
                        break;
                    case 1:
                        //edit
                        // диалоговое окно
                        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                        View promptUserView = layoutInflater.inflate(R.layout.dialog_prompt_user,null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setView(promptUserView);
                        // editText диалогового окна
                        final EditText userAnswer = promptUserView.findViewById(R.id.etUserInput);
                        userAnswer.setText(tasks.get(position).getName());
                        // Spinner диалогового окна(выгружаем данные из базы)
                        final Spinner userTagSelection = promptUserView.findViewById(R.id.spUserInput);
                        tags = db.getAllTagsDialog();
                        spinnerAdapter = new SpinnerAdapter(tags, MainActivity.this);
                        userTagSelection.setAdapter(spinnerAdapter);
                        List<String> tagNames = db.getAllTagNames();
                        Integer tagIdOfTask = tasks.get(position).getTagId();
                        Integer tagCount = db.getTagCount();
                        String tagName = tags.get(tagCount - tagIdOfTask).getName();
                        int indexSelectedTag = tagNames.indexOf(tagName);
                        userTagSelection.setSelection(indexSelectedTag);

                        alertDialogBuilder.setTitle("Изменение задачи");
                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(MainActivity.this, userAnswer.getText().toString(), Toast.LENGTH_SHORT).show();
                                Task changedTask = tasks.get(position);
                                changedTask.setName(userAnswer.getText().toString());
                                changedTask.setTagId(((Tag)userTagSelection.getSelectedItem()).getId());
                                db.updateTask(changedTask);
                                UpdateTaskList(selectedDate);
                                Toast.makeText(MainActivity.this, "Данные были изменены!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                    case 2:
                        //delete
                        db.deleteTask(tasks.get(position));
                        UpdateTaskList(selectedDate);
                        Toast.makeText(MainActivity.this, "задача удалена!", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

    }

    public void showUnfinishedTasks()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View unfinishedTasksView = layoutInflater.inflate(R.layout.dialog_unfinished_tasks,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(unfinishedTasksView);
        final ListView lstvUnfinishedTask =  unfinishedTasksView.findViewById(R.id.lstvUnfinishedTasks);
        // Здесь надо будет сделать spinner Tag'ов
        alertDialogBuilder.setTitle("Невыполненные задачи");
        // Вытаскиваем из базы все невыполненные задачи
        List<Task> unfinishedTasks = db.getAllUnfinishedTasks();
        List<Tag> tags = db.getAllTags("");
        UnfinishedTasksAdapter adapter = new UnfinishedTasksAdapter(unfinishedTasks, tags, getBaseContext());
        lstvUnfinishedTask.setAdapter(adapter);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getCorrectMonth(int month)
    {
        return String.valueOf(month+1);
    }

    public String getMonthName(int m)
    {
        switch (m)
        {
            case 1:
                return "январь";
            case 2:
                return "февраль";
            case 3:
                return "март";
            case 4:
                return "апрель";
            case 5:
                return "май";
            case 6:
                return "июнь";
            case 7:
                return "июль";
            case 8:
                return "август";
            case 9:
                return "сентябрь";
            case 10:
                return "октябрь";
            case 11:
                return "ноябрь";
            case 12:
                return "декабрь";
        }
        return "";
    }

    public void UpdateTaskList(String date)
    {
        tasks = db.getAllTasks(date);
        tags = db.getAllTags("");
        countAllTasks = db.getAllTasksCount(date);
        countFinisehdTasks = db.getAllTasksCountFinished(date);

        StatisticAdapter statisticAdapter = new StatisticAdapter(getBaseContext(), countFinisehdTasks, countAllTasks);
        lstv_head.setAdapter(statisticAdapter);

        if(tasks.size()>0) {
            tvEmptyList.setVisibility(View.GONE);
            lstv_tasks.setVisibility(View.VISIBLE);
            CustomAdapter adapter = new CustomAdapter(tasks, tags, getBaseContext());
            lstv_tasks.setAdapter(adapter);
            lstv_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getBaseContext(), taskDetail.class);
                    intent.putExtra("taskDetail", tasks.get(i));
                    intent.putExtra("tagName", tags.get(tasks.get(i).getTagId()-1).getName());
                    startActivity(intent);
                }
            });
        }
        else
        {
            lstv_tasks.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
        }
    }

    public void btnAddToDoItem_click(View view) {

        String selectedDate = dp.getDayOfMonth() + "/" + getCorrectMonth(dp.getMonth()) + "/" + dp.getYear();
        Intent intent = new Intent(this, Add_ToDoItem.class);
        intent.putExtra("currentDate", selectedDate);
        startActivity(intent);
    }
}
