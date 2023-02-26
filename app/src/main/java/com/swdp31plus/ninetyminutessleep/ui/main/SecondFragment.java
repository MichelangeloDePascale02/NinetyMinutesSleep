package com.swdp31plus.ninetyminutessleep.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentSecondBinding;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;

import java.util.ArrayList;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;

    public static SecondFragment newInstance(int index) {
        SecondFragment fragment = new SecondFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

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

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Alarm> alarms = new ArrayList<>();
        alarms.add(new Alarm("06:45"));
        alarms.add(new Alarm("09:00"));
        alarms.add(new Alarm("11:30"));
        alarms.add(new Alarm("15:20"));
        alarms.add(new Alarm("19:30"));
        alarms.add(new Alarm("20:15"));
        alarms.add(new Alarm("21:05"));
        alarms.add(new Alarm("23:30"));
        AlarmsAdapter alarmsAdapter = new AlarmsAdapter();
        alarmsAdapter.addAll(alarms);

        alarmsAdapter.setOnItemClickListener(alarm -> {
            Snackbar.make(getView(), alarm.getTime(), Snackbar.LENGTH_SHORT).show();
        });

        binding.alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.alarmsRecyclerView.setAdapter(alarmsAdapter);

        ((MainActivity) requireActivity()).getBinding().fab.setOnClickListener(view1 -> {
            Snackbar.make(getView(), "Funzione di aggiunta sveglie disponibile a breve", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}