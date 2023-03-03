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
            calendar.set(Calendar.HOUR_OF_DAY, binding.alarmTimePicker.getCurrentHour());
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
            }
        });

        binding.alarmTimePicker.setIs24HourView(true);
        binding.alarmTimePicker.setEnabled(false);

        binding.alarmTimePickerHoursCount.setText(String.format("%s %d", getString(R.string.hours_of_sleep), 0));


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

    private Date parseDate(int hour, int minute) {
        Date date = new SimpleDateFormat("hh:mm", Locale.ITALIAN).parse("" + hour + ":" + minute, new ParsePosition(0));
        Log.e("NinetyMinutesSleep - TimeParsing", "TimeParsing at SecondFragment:117, value: " + date.getHours() + ":" + date.getMinutes());
        return date;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && notFirstTimeInThisSession) {
            // info dialog building
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (preferences.getBoolean("show_90_explanation", true)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                SharedPreferences.Editor editor = preferences.edit();

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getString(R.string.explanation_90_title));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);

                TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
                textView.setText(getString(R.string.explanation_90_text));

                builder.setView(dialogView);

                CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    editor.putBoolean("show_90_explanation", !isChecked);
                    editor.apply();
                });

                Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

                final AlertDialog dialog = builder.create();

                closeBtn.setOnClickListener(v -> {
                    editor.apply();
                    dialog.dismiss();
                });

                dialog.show();
                notFirstTimeInThisSession = false;
            }
        }
    }
}