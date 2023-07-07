package com.swdp31plus.ninetyminutessleep.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.SoundsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentFirstBinding;
import com.swdp31plus.ninetyminutessleep.entities.Sound;
import com.swdp31plus.ninetyminutessleep.entities.SoundPlayer;

import java.util.ArrayList;

public class SoundFragment extends Fragment {

    private FragmentFirstBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private ArrayList<Sound> sounds;
    private ArrayList<SoundPlayer> soundsPlayers = new ArrayList<>();
    private SoundsAdapter soundsAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        //pageViewModel.setIndex(index);

        sounds = new ArrayList<>();
        soundsPlayers = new ArrayList<>();
        soundsAdapter = new SoundsAdapter();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    public static SoundFragment newInstance(int index) {
        SoundFragment fragment = new SoundFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.soundsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));

        buildSoundList();

        soundsAdapter.setContext(getContext());
        soundsAdapter.setLayoutInflater(getLayoutInflater());
        soundsAdapter.addAll(sounds);

        soundsAdapter.setOnSeekBarChangeListener((sound, i) -> {
            int index = sounds.lastIndexOf(sound);
            soundsPlayers.get(index).changeMediaPlayerVolume((float) i / 100);
        });

        soundsAdapter.setOnItemClickListener(sound -> {
            int index = sounds.lastIndexOf(sound);
            if (soundsPlayers.get(index).getPlaying()) {
                soundsPlayers.get(index).resetMediaPlayer();
                soundsPlayers.get(index).setPlaying(false);
            } else {
                soundsPlayers.get(index).startMediaPlayer();
                soundsPlayers.get(index).setPlaying(true);
            }
        });

        binding.soundsRecyclerView.setAdapter(soundsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void buildSoundList() {
        sounds.add(new Sound(getString(R.string.rain), R.raw.rain));
        sounds.add(new Sound(getString(R.string.heavy_storm), R.raw.heavy_storm));
        sounds.add(new Sound(getString(R.string.waves_on_rocks),  R.raw.waves_on_rocks));
        sounds.add(new Sound(getString(R.string.brown_noise),  R.raw.brown_noise));
        sounds.add(new Sound(getString(R.string.birds), R.raw.birds));
        sounds.add(new Sound(getString(R.string.light_breeze), R.raw.light_breeze));
        sounds.add(new Sound(getString(R.string.crackling_fire), R.raw.crackling_fire));
        //sounds.add(new Sound(getString(R.string.water_drops), R.raw.crackling_fire));
        sounds.add(new Sound(getString(R.string.hz528), R.raw.hz_528));



        sounds.add(new Sound(getString(R.string.brown_noise),  R.raw.brown_noise));
        sounds.add(new Sound(getString(R.string.birds), R.raw.birds));
        sounds.add(new Sound(getString(R.string.light_breeze), R.raw.light_breeze));
        sounds.add(new Sound(getString(R.string.crackling_fire), R.raw.crackling_fire));

        for (Sound sound : sounds) {
            soundsPlayers.add(new SoundPlayer(sound.getTitle(), getContext(), sound.getSoundRes()));
        }

        soundsAdapter.createPlayingIndex(sounds.size());
    }

    public void onTimeOutSet(int timeOut) {
        Toast.makeText(getContext(),"" + timeOut, Toast.LENGTH_SHORT).show();
        Handler h = new Handler();
            Runnable stopPlaybackRun = () -> {
                for (SoundPlayer soundPlayer : soundsPlayers) {
                    soundPlayer.resetMediaPlayer();
                    soundPlayer.setPlaying(false);
                }
                binding.soundsRecyclerView.setAdapter(soundsAdapter);
                soundsAdapter.initializePlayingIndex();
            };
        h.postDelayed(stopPlaybackRun, (long) timeOut * 1000 * 60);
    }

}