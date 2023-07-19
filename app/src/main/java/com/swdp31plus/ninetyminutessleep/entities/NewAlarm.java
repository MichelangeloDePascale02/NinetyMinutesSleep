package com.swdp31plus.ninetyminutessleep.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NewAlarm implements Parcelable {
    private int id;
    private Date time;
    private boolean active;
    private boolean recurring;
    private int[] repeatingDays;

    public NewAlarm(int id, Date time, boolean active, boolean recurring, int[] repeatingDays) {
        this.id = id;
        this.time = time;
        this.active = active;
        this.recurring = recurring;
        this.repeatingDays = repeatingDays;
    }

    protected NewAlarm(Parcel in) {
        id = in.readInt();
        time = new Date(in.readLong());
        active = in.readByte() != 0;
        recurring = in.readByte() != 0;
        repeatingDays = in.createIntArray();
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

    public boolean isRecurring() {
        return recurring;
    }

    public int[] getRepeatingDays() {
        return repeatingDays;
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
        dest.writeByte((byte) (recurring ? 1 : 0));
        dest.writeIntArray(repeatingDays);
    }
}
