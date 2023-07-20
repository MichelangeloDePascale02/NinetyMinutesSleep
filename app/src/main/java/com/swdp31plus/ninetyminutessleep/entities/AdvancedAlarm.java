package com.swdp31plus.ninetyminutessleep.entities;

import android.os.Parcel;

import java.util.Date;

public class AdvancedAlarm extends NewAlarm {

    private boolean recurring;
    private int[] repeatingDays;

    public AdvancedAlarm(int id, Date time, boolean active, boolean recurring, int[] repeatingDays) {
        super(id, time, active);
        this.recurring = recurring;
        this.repeatingDays = repeatingDays;
    }

    protected AdvancedAlarm(Parcel in) {
        super(in);
        recurring = in.readByte() != 0;
        repeatingDays = in.createIntArray();
    }

    public boolean isRecurring() {
        return recurring;
    }

    public int[] getRepeatingDays() {
        return repeatingDays;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (recurring ? 1 : 0));
        dest.writeIntArray(repeatingDays);
    }
}
