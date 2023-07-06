package com.swdp31plus.ninetyminutessleep.utilities;

import android.content.Context;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

public class SoundVolumeSeekBar extends AppCompatSeekBar {

    private String soundTitle;

    public SoundVolumeSeekBar(Context context) {
        super(context);
    }

    public String getSoundTitle() {
        return soundTitle;
    }

    public void setSoundTitle(String soundTitle) {
        this.soundTitle = soundTitle;
    }


}
