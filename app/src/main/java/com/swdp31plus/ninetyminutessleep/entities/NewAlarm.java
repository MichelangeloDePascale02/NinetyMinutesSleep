package com.swdp31plus.ninetyminutessleep.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

public class NewAlarm implements Parcelable, Serializable {
    private int id;
    private Date time;
    private String title;
    private boolean active;
    private Uri ringtoneUri;

    public NewAlarm(int id, Date time, boolean active) {
        this.id = id;
        this.time = time;
        this.active = active;
    }

    protected NewAlarm(Parcel in) {
        id = in.readInt();
        time = new Date(in.readLong());
        active = in.readByte() != 0;
        title = in.readString();
    }

    public static final Creator<NewAlarm> CREATOR = new Creator<NewAlarm>() {
        @Override
        public NewAlarm createFromParcel(Parcel in) {
            return new NewAlarm(in);
        }

        @Override
        public NewAlarm[] newArray(int size) {
            return new NewAlarm[size];
        }
    };

    public int getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Uri getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(Uri ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time.getTime());
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewAlarm newAlarm = (NewAlarm) o;

        if (id != newAlarm.id) return false;
        return Objects.equals(time, newAlarm.time);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
