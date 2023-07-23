package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentAlarmBinding;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.services.AlarmService;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import kotlin.random.Random;

public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    private View rootView;
    private double HOURS = 1.5;

    // private ArrayList<NewAlarm> alarms;
    private Date currentDate;
    private Calendar alarmDateCalendar;
    private NewAlarm currentAlarm;
    private int currentHour;
    private int currentMinute;
    private double hours_of_sleep = 0;

    public static AlarmFragment newInstance(int index) {
        AlarmFragment fragment = new AlarmFragment();
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
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    Date thirtySecondsFromNowDate; // remove


    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "QueryPermissionsNeeded"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentDate = new Date();
        alarmDateCalendar = Calendar.getInstance();
        currentAlarm = (NewAlarm) StorageUtilities.loadObject("currentAlarm.obj",getContext());

        binding.hoursPicker.setText(prepareText(currentDate.getHours()));
        binding.minutesPicker.setText(prepareText(currentDate.getMinutes()));

        updateAlarmStatus();

        binding.alarmConfirm.setOnClickListener(view14 -> {
            // Intent for AlarmService. Inside, there will be the alarm object
            Intent intent = new Intent(getActivity(), AlarmService.class);

            Log.e("Sono nel metodo di conferma", "Sono nel metodo di conferma" + alarmDateCalendar.getTime());

            NewAlarm newAlarm = new NewAlarm(
                    (int) alarmDateCalendar.getTime().getTime(),
                    alarmDateCalendar.getTime(),
                    false
            );

            intent.putExtra("alarm", (Parcelable) newAlarm);
            intent.putExtra("action","schedule");

            currentAlarm = newAlarm;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse(requireActivity().getPackageName()));
                startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            updateAlarmStatus();
            requireActivity().startService(intent);
        });

        binding.alarmDismiss.setOnClickListener(view14 -> {
            // Intent for AlarmService. Inside, there will be the alarm object
            Intent intent = new Intent(getActivity(), AlarmService.class);

            Log.e("Data", currentAlarm.getTime().toString());

            intent.putExtra("alarm", (Parcelable) new NewAlarm(
                            (int) currentAlarm.getTime().getTime(),
                            currentAlarm.getTime(),
                            true
                    )
            );
            intent.putExtra("action","dismiss");

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse(requireActivity().getPackageName()));
                startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            currentAlarm = null;
            updateAlarmStatus();

            requireActivity().startService(intent);
        });

        binding.alarmTimePickerMinus.setOnClickListener(view12 -> {
                editCycle(0);
                updateSleepCount(0);
            });
        binding.alarmTimePickerPlus.setOnClickListener(view13 -> {
                editCycle(1);
                updateSleepCount(1);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void editCycle(int param) {
        currentHour = Integer.parseInt((String) binding.hoursPicker.getText());
        currentMinute = Integer.parseInt((String) binding.minutesPicker.getText());

        if (param == 0) {
            subtractHour();
            subtractMinute();
             alarmDateCalendar.add(Calendar.SECOND,-10);
            //alarmDateCalendar.add(Calendar.MINUTE,-90);
        } else if (param == 1) {
            addHour();
            addMinute();
             alarmDateCalendar.add(Calendar.SECOND,10);
            //alarmDateCalendar.add(Calendar.MINUTE,90);
        }

        Log.e("Sono nel metodo di modifica", "Sono nel metodo di modifica" + alarmDateCalendar.getTime().toString());

        binding.hoursPicker.setText(prepareText(currentHour));
        binding.minutesPicker.setText(prepareText(currentMinute));
    }

    // 0 subtract, 1 add
    private void updateSleepCount(int param) {
        if (param == 0) {
            if (hours_of_sleep != 0) {
                hours_of_sleep -= (float) HOURS;
            } else {
                hours_of_sleep = 24 - (float) HOURS;
            }
        } else if (param == 1) {
            if (hours_of_sleep != 24) {
                hours_of_sleep += (float) HOURS;
            } else {
                hours_of_sleep = 0 + (float) HOURS;
            }
        }

        if (hours_of_sleep > 0 && hours_of_sleep <= 6)
            binding.alarmTimePickerSleepHours.setText(String.format(getResources().getString(R.string.short_sleeper), hours_of_sleep));
        else if (hours_of_sleep > 6 && hours_of_sleep <= 9)
            binding.alarmTimePickerSleepHours.setText(String.format(getResources().getString(R.string.medium_sleeper), hours_of_sleep));
        else if (hours_of_sleep > 9 && hours_of_sleep < 24)
            binding.alarmTimePickerSleepHours.setText(String.format(getResources().getString(R.string.long_sleeper), hours_of_sleep));
        else
            binding.alarmTimePickerSleepHours.setText("");
    }

    private void updateAlarmStatus() {
        if (currentAlarm != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getAvailableLocales()[0]);
            binding.alarmTimePickerCurrentAlarm.setText(
                    String.format(
                            getContext().getString(R.string.current_alarm_set),
                            sdf.format(currentAlarm.getTime())
                    )
            );
            binding.alarmConfirm.setVisibility(View.GONE);
            binding.alarmDismiss.setVisibility(View.VISIBLE);
        } else {
            binding.alarmTimePickerCurrentAlarm.setText(requireContext().getString(R.string.no_current_alarm));
            binding.alarmConfirm.setVisibility(View.VISIBLE);
            binding.alarmDismiss.setVisibility(View.GONE);
            binding.alarmTimePickerSleepHours.setText("");
        }
        StorageUtilities.saveAlarm((Serializable) currentAlarm,"currentAlarm.obj",getContext());
    }

    private void subtractHour() {
        if (currentHour > 0)
            currentHour--;
        else
            currentHour = 23;
    }

    private void subtractMinute() {
        if (currentMinute > 30)
            currentMinute -= 30;
        else {
            currentMinute += 30;
            subtractHour();
        }
    }

    private void addHour() {
        if (currentHour < 23)
            currentHour++;
        else
            currentHour = 0;
    }
    private void addMinute() {
        if (currentMinute < 30)
            currentMinute += 30;
        else {
            currentMinute -= 30;
            addHour();
        }
    }

    private String prepareText(int value) {
        StringBuilder builder = new StringBuilder();
        if (value < 10) {
            builder.append("0");
        }
        builder.append(value);
        return builder.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.hoursPicker.setText(prepareText(currentDate.getHours()));
        binding.minutesPicker.setText(prepareText(currentDate.getMinutes()));
        currentAlarm = (NewAlarm) StorageUtilities.loadObject("currentAlarm.obj", requireContext());
        alarmDateCalendar = Calendar.getInstance();
        updateAlarmStatus();
    }


}