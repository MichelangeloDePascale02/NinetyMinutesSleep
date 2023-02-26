package com.swdp31plus.ninetyminutessleep.entities;

import android.net.Uri;

public class Sound {

    private String title;
    private Uri imageUri;
    private Uri soundUri;
    private int soundRes;

    public Sound(String title, int soundRes){
        this.title = title;
        this.soundRes = soundRes;
    }

    public String getTitle() {
        return title;
    }

    public int getSoundRes() {
        return soundRes;
    }
}
