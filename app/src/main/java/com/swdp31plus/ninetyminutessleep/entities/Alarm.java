package com.swdp31plus.ninetyminutessleep.entities;

import java.io.Serializable;

public class Alarm implements Comparable, Serializable {

    private String time;

    public Alarm(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    // Yes, i know it has flaws. Good enough for now.
    // TODO: fix compareTo
    @Override
    public int compareTo(Object o) {
        return time.compareTo(o.toString());
    }
}
