package com.swdp31plus.ninetyminutessleep.ui.main;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

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

        binding.soundsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        ArrayList<Sound> sounds = new ArrayList<>();
        sounds.add(new Sound("Rain", R.raw.rain));
        sounds.add(new Sound("Heavy Storm",R.raw.heavy_storm));
        sounds.add(new Sound("Waves on Rocks",R.raw.waves_on_rocks));
        /*sounds.add(new Sound("Prova 3",0));
        sounds.add(new Sound("Prova 4",0));
        sounds.add(new Sound("Prova 5",0));
        sounds.add(new Sound("Prova 6",0));
        sounds.add(new Sound("Prova 7",0));*/
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