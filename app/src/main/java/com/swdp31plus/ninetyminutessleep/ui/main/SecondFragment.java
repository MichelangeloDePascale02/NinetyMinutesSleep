package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.swdp31plus.ninetyminutessleep.AlarmReceiver;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentSecondBinding;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.content.Context.ALARM_SERVICE;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private PageViewModel pageViewModel;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private double HOURS = 1.5;
    private double hours_of_sleep = 0;


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
        alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);
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

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
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
            calendar.set(Calendar.HOUR_OF_DAY, binding.alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, binding.alarmTimePicker.getCurrentMinute()+1);

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " - " + calendar.getTimeInMillis());
            if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
                startActivity(intent);
                Toast.makeText(getContext(), "Please confirm the alarm", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(), "There is no app that support this action", Toast.LENGTH_LONG).show();
            }
        });

        binding.alarmTimePicker.setIs24HourView(true);
        binding.alarmTimePicker.setEnabled(false);

        binding.alarmTimePickerHoursCount.setText(String.format("%s %d", getString(R.string.hours_of_sleep), 0));


        binding.alarmTimePickerMinus.setOnClickListener(view12 -> {
            long newTime = (long) (parseDate(binding.alarmTimePicker.getCurrentHour(), binding.alarmTimePicker.getCurrentMinute()).getTime() - (HOURS * 60 * 60 * 1000));
            Date newDate = new Date(newTime);
            binding.alarmTimePicker.setHour(newDate.getHours());
            binding.alarmTimePicker.setMinute(newDate.getMinutes());
            hours_of_sleep -= (float) HOURS;
            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));
        });
        binding.alarmTimePickerPlus.setOnClickListener(view13 -> {
            long newTime = (long) (parseDate(binding.alarmTimePicker.getHour(),binding.alarmTimePicker.getMinute()).getTime() + (HOURS * 60 * 60 * 1000));
            Date newDate = new Date(newTime);
            binding.alarmTimePicker.setHour(newDate.getHours());
            binding.alarmTimePicker.setMinute(newDate.getMinutes());
            hours_of_sleep += (float) HOURS;
            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Date parseDate(int hour, int minute) {
        Date date = new SimpleDateFormat("hh:mm").parse("" + hour + ":" + minute, new ParsePosition(0));
        Log.e("NinetyMinutesSleep - TimeParsing", "TimeParsing at SecondFragment:117, value: " + date.getHours() + ":" + date.getMinutes());
        return date;
    }
}