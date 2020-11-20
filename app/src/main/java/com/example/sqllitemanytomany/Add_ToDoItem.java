package com.example.sqllitemanytomany;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class Add_ToDoItem extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_IMAGE_INPUT = 1213;
    TextView tvDateCreated;
    String selectedDate;
    DataBaseHelper db;
    Spinner spTag;
    EditText etCreateLevel;
    MaterialButton btnSaveToDoLevel;
    AutoCompleteTextView actvTask;
    String taskText;
    List<Tag> tags;
    SpinnerAdapter spinnerAdapter;
    ImageButton btnMic;
    ImageView imgvPicture;
    byte[] imageBytes;
    TextView tvAttachCount;
    String imageName = "";
    Bitmap selectedImage;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__to_do_item);

        db = new DataBaseHelper(getApplicationContext());
        //imgvPicture = findViewById(R.id.imgvPicture);
        tvAttachCount = findViewById(R.id.tvAttachCount);
        //imgvPicture.setVisibility(View.GONE);
        actvTask = findViewById(R.id.actvToDo);
        selectedDate = getIntent().getExtras().getString("currentDate");

        tvDateCreated = findViewById(R.id.tvDateCreated);
        tvDateCreated.setText(selectedDate);
        etCreateLevel = findViewById(R.id.etCreateLevel);
        btnSaveToDoLevel = findViewById(R.id.btnSaveToDoLevel);
        spTag = findViewById(R.id.spLevel);
        btnMic = findViewById(R.id.btnMic);

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

        // вытаскиваем все level
        UpdateSpinner();

        // Автоподбор задач из базы
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(db.getAllTasks(), this);
        actvTask.setAdapter(autoCompleteAdapter);

        spTag = findViewById(R.id.spLevel);
        spTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==tags.size()-1)
                {
                    etCreateLevel.setVisibility(View.VISIBLE);
                    btnSaveToDoLevel.setVisibility(View.VISIBLE);
                }
                else
                {
                    etCreateLevel.setVisibility(View.GONE);
                    btnSaveToDoLevel.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void speak()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Слушаю вашу задачу...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    actvTask.setText(result.get(0));
                }
                break;
            case REQUEST_CODE_IMAGE_INPUT:
                if(resultCode == Activity.RESULT_OK && data != null)
                {
                    tvAttachCount.setText("1");
                    String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    selectedImage = BitmapFactory.decodeFile(filePath);
                    imageName = filePath.substring(filePath.lastIndexOf('/')+1 , filePath.lastIndexOf('.'));
                    //imgvPicture.setVisibility(View.VISIBLE);
                    //imgvPicture.setImageBitmap(selectedImage);
                }
        }
    }

    public void saveImageToFile()
    {
        File dir = this.getDir("imageFolder", Context.MODE_PRIVATE);
        if(!dir.exists()){ dir.mkdir(); }

        String nextImageIndex = db.getTaskCount();
        imageFile = new File(dir, imageName + nextImageIndex);
        imageBytes = DbBitmapUtility.getBytes(selectedImage);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            out.write(imageBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "сохранено!", Toast.LENGTH_SHORT).show();
    }

    public void UpdateSpinner()
    {
        tags = db.getAllTags("DESC");

        if(tags.size() == 0) {
            db.createTag(new Tag("СОЗДАТЬ СВОЙ"));
            tags = db.getAllTags("DESC");
        }

        spinnerAdapter = new SpinnerAdapter(tags, this);
        spTag.setAdapter(spinnerAdapter);
    }

    public void btnSaveToDoTask_Click(View view) {
        String tagName = ((Tag)spTag.getSelectedItem()).getName();
        Integer selectedLevelId = db.getTagIdByTagName(tagName);
        taskText = actvTask.getText().toString();
        if(!imageName.equals(""))
            saveImageToFile();
        Task t = new Task(taskText, selectedLevelId, selectedDate,"false", imageName.equals("")?"": imageFile.toString());
        long taskId = db.createTask(t);
        Toast.makeText(this, "задача сохранена! номер задачи " + taskId , Toast.LENGTH_SHORT).show();

        Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        };

        h.sendEmptyMessageDelayed(0,1500);

    }

    public void btnSaveToDoLevel_Click(View view) {
        db.createTag(new Tag(etCreateLevel.getText().toString()));
        Toast.makeText(this, "Новый приоритет успешно добавлен!", Toast.LENGTH_SHORT).show();
        UpdateSpinner();

    }

    public void btnPhotoCamera_click(View view) {
        Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
        startActivityForResult(intent, 1213);
    }

    public void btnCancelImage_Click(View view) {
        selectedImage = null;
        imageBytes = null;
        tvAttachCount.setText("0");
    }
}
