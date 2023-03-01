package com.swdp31plus.ninetyminutessleep.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

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

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    public static FirstFragment newInstance(int index) {
        FirstFragment fragment = new FirstFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (preferences.getBoolean("show_warning", true)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            SharedPreferences.Editor editor = preferences.edit();

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_warning,null);

            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(getString(R.string.warning_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            builder.setView(dialogView);

            CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_warning);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editor.putBoolean("show_warning", !isChecked);
                editor.apply();
            });

            Button closeBtn = dialogView.findViewById(R.id.button_dialog_warning);

            final AlertDialog dialog = builder.create();

            closeBtn.setOnClickListener(v -> {
                editor.apply();
                dialog.dismiss();
            });

            dialog.show();
        }

        binding.soundsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        ArrayList<Sound> sounds = new ArrayList<>();
        sounds.add(new Sound("Rain", R.raw.rain));
        sounds.add(new Sound("Heavy Storm", R.raw.heavy_storm));
        sounds.add(new Sound("Waves on Rocks",  R.raw.waves_on_rocks));
        SoundsAdapter soundsAdapter = new SoundsAdapter();
        soundsAdapter.setContext(getContext());
        soundsAdapter.addAll(sounds);

        binding.soundsRecyclerView.setAdapter(soundsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}