package com.example.sqllitemanytomany;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {

    private Integer Id;
    private String Name;

    public Tag()
    {

    }

    public Tag(String name) {
        Name = name;
    }

    protected Tag(Parcel in) {
        if (in.readByte() == 0) {
            Id = null;
        } else {
            Id = in.readInt();
        }
        Name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (Id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Id);
        }
        dest.writeString(Name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getId() { return Id; }

    public void setId(Integer id) { Id = id; }



}
