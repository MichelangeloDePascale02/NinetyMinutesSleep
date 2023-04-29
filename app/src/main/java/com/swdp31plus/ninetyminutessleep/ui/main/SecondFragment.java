package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentSecondBinding;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private double HOURS = 1.5;
    private int currentHour;
    private int currentMinute;
    private double hours_of_sleep = 0;
    private boolean notFirstTimeInThisSession = true;

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

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        Date currentDate = new Date();

        binding.hoursPicker.setText(""+currentDate.getHours());
        binding.minutesPicker.setText(""+currentDate.getMinutes());
        return rootView;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "QueryPermissionsNeeded"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*ArrayList<Alarm> alarms = new ArrayList<>();
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
*/
        ((MainActivity) requireActivity()).getBinding().fab.setImageResource(R.drawable.baseline_check_24);

        ((MainActivity) requireActivity()).getBinding().fab.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();

            // calendar is called to get current time in hour and minute
            try {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) binding.hoursPicker.getText()));
                calendar.set(Calendar.MINUTE, Integer.parseInt((String) binding.minutesPicker.getText()));
                // using intent i have class AlarmReceiver class which inherits
                // BroadcastReceiver
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                intent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " - " + calendar.getTimeInMillis());
                if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
                    startActivity(intent);
                    Toast.makeText(getContext(), "Please confirm the alarm", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "There is no app that support this action", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException nfe) {
                Toast.makeText(getContext(),"CHAR",Toast.LENGTH_LONG).show();
            }
        });

        binding.alarmTimePickerHoursCount.setText(String.format("%s %d", getString(R.string.hours_of_sleep), 0));


        binding.alarmTimePickerMinus.setOnClickListener(view12 -> {
            editCycle(0);
            if (hours_of_sleep != 0) {
                hours_of_sleep -= (float) HOURS;
            } else {
                hours_of_sleep = 24 - (float) HOURS;
            }
            if (hours_of_sleep == 0)
                binding.alarmTimePickerHoursScore.setText("");
            else if (hours_of_sleep > 0 && hours_of_sleep <= 6)
                binding.alarmTimePickerHoursScore.setText(R.string.short_sleeper);
            else if (hours_of_sleep > 6 && hours_of_sleep <= 9)
                binding.alarmTimePickerHoursScore.setText(R.string.medium_sleeper);
            else
                binding.alarmTimePickerHoursScore.setText(R.string.long_sleeper);

            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));
        });
        binding.alarmTimePickerPlus.setOnClickListener(view13 -> {
            editCycle(1);
            if (hours_of_sleep != 24) {
                hours_of_sleep += (float) HOURS;
            } else {
                hours_of_sleep = 0 + (float) HOURS;
            }
            if (hours_of_sleep == 0)
                binding.alarmTimePickerHoursScore.setText("");
            else if (hours_of_sleep > 0 && hours_of_sleep <= 6)
                binding.alarmTimePickerHoursScore.setText(R.string.short_sleeper);
            else if (hours_of_sleep > 6 && hours_of_sleep <= 9)
                binding.alarmTimePickerHoursScore.setText(R.string.medium_sleeper);
            else
                binding.alarmTimePickerHoursScore.setText(R.string.long_sleeper);
            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));
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