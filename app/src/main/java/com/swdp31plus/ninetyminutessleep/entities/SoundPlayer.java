package com.swdp31plus.ninetyminutessleep.entities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.swdp31plus.ninetyminutessleep.R;

public class SoundPlayer {

    private String title;
    private MediaPlayer mp;
    private boolean isPlaying;
    private Context context;
    private int res;

    public SoundPlayer(String title, Context context, int res) {
        this.title = title;
        this.mp = MediaPlayer.create(context, res);
        this.isPlaying = false;

        this.context = context;
        this.res = res;
    }

    public boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void resetMediaPlayer() {
        mp.stop();
        mp.reset();
        mp.release();
        this.mp = null;
        this.mp = MediaPlayer.create(context, res);
    }

    public void startMediaPlayer() {
        mp.start();
        mp.setLooping(true);
    }
}
