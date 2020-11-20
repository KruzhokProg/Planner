package com.example.sqllitemanytomany;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private Integer Id;
    private String Name;
    private Integer TagId;
    private String CreateAt;
    private String Finished;
    private String ImagePath;

    public Task()
    {

    }

    public Task(String name, Integer tagId, String createAt, String finished, String imagePath) {
        Name = name;
        TagId = tagId;
        CreateAt = createAt;
        Finished = finished;
        ImagePath = imagePath;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getTagId() {
        return TagId;
    }

    public void setTagId(Integer tagId) {
        TagId = tagId;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    public String getFinished() {
        return Finished;
    }

    public void setFinished(String finished) {
        Finished = finished;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }


    protected Task(Parcel in) {
        if (in.readByte() == 0) {
            Id = null;
        } else {
            Id = in.readInt();
        }
        Name = in.readString();
        if (in.readByte() == 0) {
            TagId = null;
        } else {
            TagId = in.readInt();
        }
        CreateAt = in.readString();
        Finished = in.readString();
        ImagePath = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (Id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(Id);
        }
        parcel.writeString(Name);
        if (TagId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(TagId);
        }
        parcel.writeString(CreateAt);
        parcel.writeString(Finished);
        parcel.writeString(ImagePath);
    }
}
