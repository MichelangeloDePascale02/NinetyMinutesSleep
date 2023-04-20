package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentAlarmListBinding;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;
import com.swdp31plus.ninetyminutessleep.ui.main.dialogs.DialogAddAlarmFragment;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

public class AlarmListFragment extends Fragment implements DialogAddAlarmFragment.GetData {

    private FragmentAlarmListBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private ArrayList<Alarm> alarms;
    private DialogAddAlarmFragment dialogAddAlarmFragment;

    public static AlarmListFragment newInstance(int index) {
        AlarmListFragment fragment = new AlarmListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

        alarms = new ArrayList<>();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAlarmListBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "QueryPermissionsNeeded"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alarms.add(new Alarm("06:45"));
        alarms.add(new Alarm("09:00"));
        alarms.add(new Alarm("11:30"));
        alarms.add(new Alarm("15:20"));
        AlarmsAdapter alarmsAdapter = new AlarmsAdapter();
        alarmsAdapter.addAll(alarms);

        alarmsAdapter.setOnItemClickListener(alarm -> {
            Snackbar.make(getView(), alarm.getTime(), Snackbar.LENGTH_SHORT).show();
        });

        binding.alarmsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.alarmsListRecyclerView.setAdapter(alarmsAdapter);

        binding.addAlarmToListButton.setOnClickListener(v -> {
            dialogAddAlarmFragment = new DialogAddAlarmFragment();
            dialogAddAlarmFragment.setListener(this);
            dialogAddAlarmFragment.show(getParentFragmentManager(), "DialogAddAlarmFragment");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDialogDismissed(Alarm alarm) {

    }
}