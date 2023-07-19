package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentAlarmBinding;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.services.AlarmService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kotlin.random.Random;

public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    private View rootView;
    private double HOURS = 1.5;
    private ArrayList<NewAlarm> alarms;
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

        Date currentDate = new Date();

        binding.hoursPicker.setText(prepareText(currentDate.getHours()));
        binding.minutesPicker.setText(prepareText(currentDate.getMinutes()));
        return rootView;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "QueryPermissionsNeeded"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.alarmConfirm.setOnClickListener(view14 -> {
            // Intent for AlarmService. Inside, there will be the alarm object
            Intent intent = new Intent(getActivity(), AlarmService.class);

            Calendar thirtySecondsFromNowCalendar = Calendar.getInstance();
            thirtySecondsFromNowCalendar.add(Calendar.SECOND,5);
            Date thirtySecondsFromNowDate = thirtySecondsFromNowCalendar.getTime();

            Log.e("Data",thirtySecondsFromNowDate.toString());

            intent.putExtra("alarm", new NewAlarm(
                            Random.Default.nextInt((int) (System.currentTimeMillis() / 1000L)),
                            thirtySecondsFromNowDate,
                            false,
                            false,
                            null
                    )
            );

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse(requireActivity().getPackageName()));
                startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }

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
        } else if (param == 1) {
            addHour();
            addMinute();
        }

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
}