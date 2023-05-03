package com.swdp31plus.ninetyminutessleep.entities;

import java.io.Serializable;
import java.util.UUID;

public class Alarm implements Comparable, Serializable {

    private int uniqueID;
    private String time;
    private String hour;
    private String minute;

    private int MIN = 1000;

    private int MAX = 9999;

    public Alarm(String hour, String minute) {
        this.uniqueID = (int)Math.floor(Math.random() * (MAX - MIN + 1) + MIN);
        this.hour = hour;
        this.minute = minute;
        this.time = hour + ":" + minute;
    }

    public int getUniqueID(){
        return uniqueID;
    }

    public String getTime() {
        return time;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    // Yes, i know it has flaws. Good enough for now.
    // TODO: fix compareTo
    @Override
    public int compareTo(Object o) {
        return - time.compareTo(o.toString());
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "uniqueID=" + uniqueID +
                ", time='" + time + '\'' +
                '}';
    }
}
