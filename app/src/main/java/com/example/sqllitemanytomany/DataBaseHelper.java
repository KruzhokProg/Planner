package com.example.sqllitemanytomany;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    static final String dbName = "todoDB";

    // Создаем тамблицу задач Task
    static final String taskTable = "Task";
    static final String colTaskId = "TaskId";
    static final String colTaskName = "TaskName";
    static final String colTaskTagId = "TaskTagId";
    static final String colTaskCreateAt = "TaskCreateAt";
    static final String colTaskFinished = "TaskFinished";
    static final String colTaskPicture = "TaskPicture";

    // Создаем таблицу Тегов Tag
    static final String tagTable = "Tag";
    static final String colTagId = "TagId";
    static final String colTagName = "TagName";

    public DataBaseHelper(Context context) {
        super(context, dbName, null, 13);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tagTable + " (" +
                colTagId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                colTagName + " TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + taskTable + " (" +
                colTaskId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                colTaskName + " TEXT, " + colTaskCreateAt + " TEXT, " + colTaskFinished + " TEXT, " +
                colTaskPicture + " BLOB, " +
                colTaskTagId + " TEXT NOT NULL, FOREIGN KEY (" + colTaskTagId  +") REFERENCES "+ tagTable + " (" + colTagId + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+taskTable);
        db.execSQL("DROP TABLE IF EXISTS "+tagTable);
        onCreate(db);
    }

    public long createTag(Tag tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colTagName,tag.getName());
        long newTagId = db.insert(tagTable,null,cv);
        return newTagId;
    }

    public int getTagIdByTagName(String tagName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + colTagId + " FROM " + tagTable + " WHERE " + colTagName + "='" + tagName + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Integer tagId = c.getInt(c.getColumnIndex(colTagId));
        return  tagId;
    }

    public int getTagCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + tagTable;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null)
            c.moveToFirst();
        Integer count = c.getInt(c.getColumnIndex("count"));
        return count;
    }

    public String getTaskCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + taskTable;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c!=null)
            c.moveToFirst();
        return String.valueOf(c.getInt(c.getColumnIndex("count")) + 1);
    }

    public int getAllTasksCount(String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + taskTable + " WHERE " + colTaskCreateAt + "='" + date + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        Integer count = c.getInt(c.getColumnIndex("count"));
        return count;
    }

    public int getAllTasksCountFinished(String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COUNT(*) as 'count' FROM " + taskTable + " WHERE " + colTaskCreateAt + "='" + date + "' AND " + colTaskFinished + "='true'";
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        Integer count = c.getInt(c.getColumnIndex("count"));
        return count;
    }

    public long createTask(Task task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colTaskName, task.getName());
        cv.put(colTaskTagId, task.getTagId());
        cv.put(colTaskCreateAt, task.getCreateAt());
        cv.put(colTaskFinished, task.getFinished());
        cv.put(colTaskPicture, task.getImagePath());
        long newTaskId = db.insert(taskTable,null, cv);
        return newTaskId;
    }

    public List<Task> getAllUnfinishedTasks()
    {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + taskTable + " WHERE " + colTaskFinished + "='false' " + " GROUP BY " + colTaskName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst())
        {
            do{
                Task task = new Task();
                //task.setId(c.getInt(c.getColumnIndex(colTaskId)));
                task.setName(c.getString(c.getColumnIndex(colTaskName)));
                task.setTagId(c.getInt(c.getColumnIndex(colTaskTagId)));
                //task.setCreateAt(c.getString(c.getColumnIndex(colTaskCreateAt)));
                //task.setTagName(c.getString(c.getColumnIndex(colTaskTag)));
                tasks.add(task);
            }while(c.moveToNext());
        }

        return tasks;
    }

    public List<Task> getAllTasks()
    {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + colTaskName + " FROM " + taskTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst())
        {
            do{
                Task task = new Task();
                //task.setId(c.getInt(c.getColumnIndex(colTaskId)));
                task.setName(c.getString(c.getColumnIndex(colTaskName)));
                //task.setCreateAt(c.getString(c.getColumnIndex(colTaskCreateAt)));
                //task.setTagName(c.getString(c.getColumnIndex(colTaskTag)));
                tasks.add(task);
            }while(c.moveToNext());
        }

        return tasks;
    }

    public List<Statistic> getGrapthData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String t1 = "(SELECT " + colTaskCreateAt + " ,COUNT(*)*1.0 as 'done' FROM " + taskTable +
                " WHERE " + colTaskFinished + "='true' GROUP BY " + colTaskCreateAt + ") as t1";
        String t2 = "(SELECT " + colTaskCreateAt + " ,COUNT(*)*1.0 as 'total' FROM " + taskTable +
                " GROUP BY " + colTaskCreateAt + ") as t2";
        String joinTable = "(SELECT t1." + colTaskCreateAt + " ,done, total FROM " + t1 + " join " + t2 +
                " ON t1." + colTaskCreateAt + "= t2." + colTaskCreateAt + ") as t";
        String statisticQuery = "SELECT t." + colTaskCreateAt + " as 'date' , t.done/t.total as 'percent' FROM" + joinTable;

        Cursor c = db.rawQuery(statisticQuery, null);
        List<Statistic> statistics = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                // вытаскиваем всю дату и разбиваем на день и месяц
                String date =  c.getString(c.getColumnIndex("date"));
                String[] dateMas = date.split("[/]");
                Integer day = Integer.parseInt(dateMas[0]);
                Integer month = Integer.parseInt(dateMas[1]);
                Float percent = 100 * Float.parseFloat(c.getString(c.getColumnIndex("percent")));
                Statistic s = new Statistic();
                s.setDay(day);
                s.setMonth(month);
                s.setPercent(percent);
                statistics.add(s);
            }while(c.moveToNext());
        }
        return statistics;
    }

    public List<Task> getAllTasks(String selectedDate)
    {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT * FROM " + taskTable + " WHERE " + colTaskCreateAt + "= '" + selectedDate + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst())
        {
            do{
                Task task = new Task();
                task.setId(c.getInt(c.getColumnIndex(colTaskId)));
                task.setName(c.getString(c.getColumnIndex(colTaskName)));
                task.setCreateAt(c.getString(c.getColumnIndex(colTaskCreateAt)));
                task.setTagId(c.getInt(c.getColumnIndex(colTaskTagId)));
                task.setFinished(c.getString(c.getColumnIndex(colTaskFinished)));
                task.setImagePath(c.getString(c.getColumnIndex(colTaskPicture)));
                tasks.add(task);
            }while(c.moveToNext());
        }

        return tasks;
    }

    public List<Tag> getAllTags(String mode)
    {
        List<Tag> tags = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tagTable + " ORDER BY " + colTagId + " " + mode;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c.moveToFirst()) {
            do{
                Tag tag = new Tag();
                tag.setName(c.getString(c.getColumnIndex(colTagName)));
                tag.setId(c.getInt(c.getColumnIndex(colTagId)));
                tags.add(tag);
            }while(c.moveToNext());
        }

        return tags;
    }

    public List<Tag> getAllTagsDialog()
    {
        List<Tag> tags = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tagTable + " WHERE " + colTagName + "!='" + "СОЗДАТЬ СВОЙ" + "' ORDER BY " + colTagId + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c.moveToFirst()) {
            do{
                Tag tag = new Tag();
                tag.setName(c.getString(c.getColumnIndex(colTagName)));
                tag.setId(c.getInt(c.getColumnIndex(colTagId)));
                tags.add(tag);
            }while(c.moveToNext());
        }

        return tags;
    }

    public  List<String> getAllTagNames()
    {
        List<String> tagNames = new ArrayList<>();
        String selectQuery = "SELECT " + colTagName + " FROM " + tagTable + " ORDER BY " + colTagId + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c.moveToFirst()) {
            do{
                tagNames.add(c.getString(c.getColumnIndex(colTagName)));
            }while(c.moveToNext());
        }
        return tagNames;
    }

    public void deleteTask(Task t)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(taskTable, colTaskId + "=?", new String[]{String.valueOf(t.getId())} );
    }

    public int updateTask(Task task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colTaskName, task.getName());
        cv.put(colTaskTagId, task.getTagId());
        cv.put(colTaskCreateAt, task.getCreateAt());
        cv.put(colTaskFinished, task.getFinished());
        cv.put(colTaskPicture, task.getImagePath());
        return db.update(taskTable, cv, colTaskId + "=?", new String[]{String.valueOf(task.getId())});
    }

}
