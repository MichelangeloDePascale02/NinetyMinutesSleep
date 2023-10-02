package com.swdp31plus.ninetyminutessleep.ui.fragments.alarms;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;

import java.util.Date;

public class CreateAlarmDialog extends DialogFragment {

    private CommunicationInterface listener;
    private Date currentTime;
    private Date alarmTime;
    private int timeIterations = 0;
    private float sleepHours = 0;
    private TextView hoursTextView;
    private TextView minutesTextView;
    private TextView sleepHoursGoalTextView;

    public interface CommunicationInterface {
        void onTimeSelected(Date alarmDate);
    }

    public void setListener(CommunicationInterface listener) {this.listener = listener; }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_alarm, null);
        builder.setView(root);

        hoursTextView = root.findViewById(R.id.hours_textview);
        minutesTextView = root.findViewById(R.id.minutes_textview);
        sleepHoursGoalTextView = root.findViewById(R.id.sleepHoursSuggestionTextView);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
        titleText.setText(getString(R.string.new_alarm));
        titleText.setTextSize(22);
        builder.setCustomTitle(titleView);

        currentTime = new Date();
        currentTime.setSeconds(0);
        Log.e("CreateAlarmDialog","Data normalizzata: " + currentTime.toString());
        Log.e("CreateAlarmDialog","Contatore a: " + timeIterations);
        alarmTime = currentTime;
        time(currentTime);

        root.findViewById(R.id.alarmTimePickerMinus).setOnClickListener(view12 -> {
            timeIterations--;
            sleepHours -= 1.5;
            //alarmTime.setMinutes(alarmTime.getMinutes() - 90);
            alarmTime.setMinutes(alarmTime.getMinutes() - 1);
            //alarmTime.setSeconds(alarmTime.getSeconds() - 10);
            Log.e("CreateAlarmDialog","Rimossi 90 minuti: " + currentTime.toString());
            Log.e("CreateAlarmDialog","Contatore diminuito a: " + timeIterations);
            Log.e("CreateAlarmDialog","sleepHours a: " + sleepHours);
            time(currentTime);
            updateSuggestion();
        });
        root.findViewById(R.id.alarmTimePickerPlus).setOnClickListener(view13 -> {
            timeIterations++;
            sleepHours += 1.5;
            //alarmTime.setMinutes(alarmTime.getMinutes() + 90);
            alarmTime.setMinutes(alarmTime.getMinutes() + 1);
            //alarmTime.setSeconds(alarmTime.getSeconds() + 10);
            Log.e("CreateAlarmDialog","Aggiunti 90 minuti: " + currentTime.toString());
            Log.e("CreateAlarmDialog","Contatore aumentato a: " + timeIterations);
            Log.e("CreateAlarmDialog","sleepHours a: " + sleepHours);
            time(currentTime);
            updateSuggestion();
        });

        root.findViewById(R.id.alarmConfirm).setOnClickListener(view13 -> {
            int offset = timeIterations / 16;
            if (offset < 0) {
                while (offset != 0) {
                    currentTime.setHours(currentTime.getHours() + 24);
                    offset++;
                }
            } else if (offset > 0) {
                while (offset != 0) {
                    currentTime.setHours(currentTime.getHours() - 24);
                    offset--;
                }
            }
            listener.onTimeSelected(currentTime);
            dismiss();
        });

        return builder.create();
    }

    /**
     * Used to set the text on the Dialog's TextViews to display the time.
     * Uses prepareText(int value) to properly format hours and minutes.
     *
     * @param dateToSet the current date the user has choosen
     */
    private void time(Date dateToSet) {
        hoursTextView.setText(prepareText(dateToSet.getHours()));
        minutesTextView.setText(prepareText(dateToSet.getMinutes()));
    }

    /**
     * Method used to display the appropriate "suggestion" message, based on the hours of sleep the
     * user is putting in.
     * Real sleep hours are calculated using (mod 24). This allow us to ignore full day offsets
     * (not accepted in this app). More info about this mechanism can be found in alarmConfirm
     * onClickListener method.
     * */
    private void updateSuggestion() {
        float normalizedHours = sleepHours % 24;
        Log.e("CreateAlarmDialog","normalizedHours a: " + normalizedHours);

        if (normalizedHours > 0 && normalizedHours <= 6)
            sleepHoursGoalTextView.setText(String.format(getResources().getString(R.string.short_sleeper), normalizedHours));
        else if (normalizedHours > 6 && normalizedHours <= 9)
            sleepHoursGoalTextView.setText(String.format(getResources().getString(R.string.medium_sleeper), normalizedHours));
        else if (normalizedHours > 9 && normalizedHours < 24)
            sleepHoursGoalTextView.setText(String.format(getResources().getString(R.string.long_sleeper), normalizedHours));
        else
            sleepHoursGoalTextView.setText("");
    }
    private String prepareText(int value) {
        StringBuilder builder = new StringBuilder();
        if (value < 10) { // TODO: should check for negative numbers, just to make it safe
            builder.append("0");
        }
        builder.append(value);
        return builder.toString();
    }

}
