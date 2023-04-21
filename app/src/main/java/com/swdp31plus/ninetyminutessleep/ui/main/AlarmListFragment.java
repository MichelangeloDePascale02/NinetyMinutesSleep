package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.swdp31plus.ninetyminutessleep.AlarmActivity;
import com.swdp31plus.ninetyminutessleep.AlarmReceiver;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentAlarmListBinding;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;
import com.swdp31plus.ninetyminutessleep.ui.main.dialogs.DialogAddAlarmFragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Context.ALARM_SERVICE;

public class AlarmListFragment extends Fragment implements DialogAddAlarmFragment.GetData {

    private FragmentAlarmListBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private ArrayList<Alarm> alarms;
    private DialogAddAlarmFragment dialogAddAlarmFragment;
    private AlarmsAdapter alarmsAdapter;
    private AlarmManager alarmManager;

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

        if (loadAlarms("savedAlarms.obj") == null) {
            alarms = new ArrayList<>();
        } else {
            alarms = (ArrayList<Alarm>) loadAlarms("savedAlarms.obj");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAlarmListBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        alarmsAdapter = new AlarmsAdapter();
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        return rootView;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "QueryPermissionsNeeded"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        binding.test.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(getContext(), AlarmActivity.class);
            requireActivity().startActivity(notificationIntent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDialogDismissed(String hour, String minute) {
        hour = "01";
        minute = "58";
        scheduleAlarm(Integer.parseInt(hour), Integer.parseInt(minute));
        Toast.makeText(getContext(),"Alarm set at " + hour + ":" + minute, Toast.LENGTH_LONG).show();
        Alarm alarm = new Alarm(
                new StringBuilder()
                        .append(hour)
                        .append(":")
                        .append(minute)
                        .toString());
        alarms.add(alarm);
        alarmsAdapter.add(alarm);
        alarmsAdapter.sort();
        alarmsAdapter.notifyDataSetChanged();
        //saveAlarms(alarms, "savedAlarms.obj");
    }

    private void scheduleAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // using intent i have class AlarmReceiver class which inherits
        // BroadcastReceiver
        Intent intent = new Intent(getContext(), AlarmReceiver.class);

        // we call broadcast using pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, FLAG_IMMUTABLE);

        long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            // setting time as AM and PM
            if (Calendar.AM_PM == 0)
                time = time + (1000 * 60 * 60 * 12);
            else
                time = time + (1000 * 60 * 60 * 24);
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public void saveAlarms(Object obj, String path) {
        FileOutputStream fos = null;
        try {
            fos = requireContext().openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(fos);
                os.writeObject(obj);
                os.close();
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(),"File not saved", Toast.LENGTH_LONG).show();
        }
    }
    public Object loadAlarms(String path) throws NullPointerException {
        FileInputStream fis = null;
        ArrayList<Alarm> readAlarmList = null;
        try {
            fis = getContext().openFileInput(path);
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(fis);
                readAlarmList = (ArrayList<Alarm>) is.readObject();
                is.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(),"File not loaded", Toast.LENGTH_LONG).show();
        }
        return readAlarmList;
    }
}