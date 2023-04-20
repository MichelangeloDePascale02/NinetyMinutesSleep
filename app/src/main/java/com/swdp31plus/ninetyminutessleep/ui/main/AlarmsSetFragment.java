package com.swdp31plus.ninetyminutessleep.ui.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.swdp31plus.ninetyminutessleep.AlarmReceiver;
import com.swdp31plus.ninetyminutessleep.MainActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentAlarmSetBinding;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

public class AlarmsSetFragment extends Fragment {
    private FragmentAlarmSetBinding binding;
    private View rootView;
    private double HOURS = 1.5;
    private double hours_of_sleep = 0;
    private AlarmManager alarmManager;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentAlarmSetBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).getBinding().fab.setImageResource(R.drawable.baseline_check_24);

        ((MainActivity) requireActivity()).getBinding().fab.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();

            // calendar is called to get current time in hour and minute
            /*calendar.set(Calendar.HOUR_OF_DAY, binding.alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, binding.alarmTimePicker.getCurrentMinute());

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
            }*/



            // calendar is called to get current time in hour and minute
            calendar.set(Calendar.HOUR_OF_DAY, binding.alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, binding.alarmTimePicker.getCurrentMinute());

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
            /*if (true) {*/
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            /*} else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 86400000, pendingIntent);
            }*/


            Toast.makeText(getContext(), "ALARM ON at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
            // alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);

        });

        binding.alarmTimePicker.setIs24HourView(true);
        //binding.alarmTimePicker.setEnabled(false);

        //binding.alarmTimePickerHoursCount.setText(String.format("%s %d", getString(R.string.hours_of_sleep), 0));


        binding.alarmTimePickerMinus.setOnClickListener(view12 -> {
            Date newDate = new Date((long) (parseDate(binding.alarmTimePicker.getCurrentHour(), binding.alarmTimePicker.getCurrentMinute()).getTime() - (HOURS * 60 * 60 * 1000)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            binding.alarmTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            binding.alarmTimePicker.setMinute(calendar.get(Calendar.MINUTE));
            if (hours_of_sleep != 0) {
                hours_of_sleep -= (float) HOURS;
            } else {
                hours_of_sleep = 24 - (float) HOURS;
            }
            /*if (hours_of_sleep == 0)
                binding.alarmTimePickerHoursScore.setText("");
            else if (hours_of_sleep > 0 && hours_of_sleep <= 6)
                binding.alarmTimePickerHoursScore.setText(R.string.short_sleeper);
            else if (hours_of_sleep > 6 && hours_of_sleep <= 9)
                binding.alarmTimePickerHoursScore.setText(R.string.medium_sleeper);
            else
                binding.alarmTimePickerHoursScore.setText(R.string.long_sleeper);

            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));*/
        });
        binding.alarmTimePickerPlus.setOnClickListener(view13 -> {
            Date newDate = new Date((long) (parseDate(binding.alarmTimePicker.getCurrentHour(), binding.alarmTimePicker.getCurrentMinute()).getTime() + (HOURS * 60 * 60 * 1000)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            binding.alarmTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            binding.alarmTimePicker.setMinute(calendar.get(Calendar.MINUTE));
            if (hours_of_sleep != 24) {
                hours_of_sleep += (float) HOURS;
            } else {
                hours_of_sleep = 0 + (float) HOURS;
            }
            /*if (hours_of_sleep == 0)
                binding.alarmTimePickerHoursScore.setText("");
            else if (hours_of_sleep > 0 && hours_of_sleep <= 6)
                binding.alarmTimePickerHoursScore.setText(R.string.short_sleeper);
            else if (hours_of_sleep > 6 && hours_of_sleep <= 9)
                binding.alarmTimePickerHoursScore.setText(R.string.medium_sleeper);
            else
                binding.alarmTimePickerHoursScore.setText(R.string.long_sleeper);
            binding.alarmTimePickerHoursCount.setText(String.format("%s %,.2f", getString(R.string.hours_of_sleep), hours_of_sleep));*/
        });
    }

    private Date parseDate(int hour, int minute) {
        Date date = new SimpleDateFormat("hh:mm", Locale.ITALIAN).parse("" + hour + ":" + minute, new ParsePosition(0));
        Log.e("NinetyMinutesSleep - TimeParsing", "TimeParsing at SecondFragment:117, value: " + date.getHours() + ":" + date.getMinutes());
        return date;
    }
}
