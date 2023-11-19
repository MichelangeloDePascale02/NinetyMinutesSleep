package com.swdp31plus.ninetyminutessleep.ui.main.pomodoro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.swdp31plus.ninetyminutessleep.ui.main.sounds.SoundFragment;

public class PomodoroFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static PomodoroFragment newInstance(int index) {
        PomodoroFragment fragment = new PomodoroFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
}
