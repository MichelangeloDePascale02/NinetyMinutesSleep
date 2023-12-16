package com.swdp31plus.ninetyminutessleep.ui.main.sounds;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.swdp31plus.ninetyminutessleep.databinding.FragmentSoundBinding;
import com.swdp31plus.ninetyminutessleep.entities.Sound;
import com.swdp31plus.ninetyminutessleep.entities.SoundPlayer;
import com.swdp31plus.ninetyminutessleep.ui.main.PageViewModel;

import java.util.ArrayList;

public class SoundFragment extends Fragment {

    private FragmentSoundBinding binding;
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

        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        assert getArguments() != null;
        pageViewModel.setIndex(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSoundBinding.inflate(inflater, container, false);
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

        sounds = new ArrayList<>();
        soundsPlayers = new ArrayList<>();
        soundsAdapter = new SoundsAdapter();

        // warning dialog building
        SharedPreferences preferences = getActivity().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
        if (preferences.getBoolean("show_warning", true)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            SharedPreferences.Editor editor = preferences.edit();

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(getString(R.string.warning_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
            textView.setText(getString(R.string.warning_text));

            builder.setView(dialogView);

            CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editor.putBoolean("show_warning", !isChecked);
                editor.apply();
            });

            Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

            final AlertDialog dialog = builder.create();

            closeBtn.setOnClickListener(v -> {
                editor.apply();
                dialog.dismiss();
            });

            dialog.show();
        }

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
        sounds.add(new Sound(getString(R.string.hz528), R.raw.hz_528));
        sounds.add(new Sound(getString(R.string.fan), R.raw.fan));

        /*sounds.add(new Sound("TEST", R.raw.crackling_fire));
        sounds.add(new Sound("TEST", R.raw.light_breeze));
        sounds.add(new Sound("TEST", R.raw.crackling_fire));
        sounds.add(new Sound("TEST", R.raw.light_breeze));
        sounds.add(new Sound("TEST", R.raw.crackling_fire));
        sounds.add(new Sound("TEST", R.raw.light_breeze));
        sounds.add(new Sound("TEST", R.raw.crackling_fire));*/

        for (Sound sound : sounds) {
            soundsPlayers.add(new SoundPlayer(sound.getTitle(), getContext(), sound.getSoundRes()));
        }

        soundsAdapter.createPlayingIndex(sounds.size());
    }

    public void onTimeOutSet(int timeOut) {
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
        Toast.makeText(getContext(),String.format(getResources().getString(R.string.stop_sounds_timer), timeOut), Toast.LENGTH_LONG).show();
    }

}