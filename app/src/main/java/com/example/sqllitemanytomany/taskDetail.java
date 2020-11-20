package com.example.sqllitemanytomany;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class taskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Task task = getIntent().getExtras().getParcelable("taskDetail");
        String tagName = getIntent().getExtras().getString("tagName");

        TextView tvTaskDetail = findViewById(R.id.tvTaskDetail);
        TextView tvTaskTagDetail = findViewById(R.id.tvTaskTagDetail);
        ImageView imgvDetail = findViewById(R.id.imgvDetail);

        // считываем из файла оригинальное изображение
        String path = task.getImagePath();
        File f = new File(path);
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream buf = new BufferedInputStream(fis);
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap image = DbBitmapUtility.getImage(bytes);

        // выводим данные
        tvTaskDetail.setText(task.getName());
        tvTaskTagDetail.setText(tagName);
        imgvDetail.setImageBitmap(image);
    }
}
