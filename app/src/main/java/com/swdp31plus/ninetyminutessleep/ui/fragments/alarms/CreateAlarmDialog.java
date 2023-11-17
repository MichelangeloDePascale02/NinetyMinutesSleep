package com.swdp31plus.ninetyminutessleep.ui.fragments.alarms;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;

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
    private Uri selectedMp3Uri;

    public interface CommunicationInterface {
        void onTimeSelected(Date alarmDate);
        void onTimeSelected(NewAlarm newAlarm);
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
        currentTime.setMinutes(currentTime.getMinutes() + 1);
        Log.d("CreateAlarmDialog","Data normalizzata " + currentTime.toString() + " e contatore a " + timeIterations);
        alarmTime = currentTime;
        time(currentTime);

        SharedPreferences preferences = getContext().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
        float selectedInterval = preferences.getInt("selectedInterval",90);
        float sleepHoursModifier = selectedInterval / 60;

        Log.d("Log in CreateAlarmDialog", "selectedInterval è " + selectedInterval + " e quindi sleepHoursModifier è " + sleepHoursModifier);

        root.findViewById(R.id.alarmTimePickerRingtonePicker).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, 1);
        });

        root.findViewById(R.id.alarmTimePickerRingtonePicker).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),getString(R.string.ringtone_selector_hint),Toast.LENGTH_LONG).show();
                return false;
            }
        });

        root.findViewById(R.id.alarmTimePickerMinus).setOnClickListener(view12 -> {
            timeIterations--;
            sleepHours -= sleepHoursModifier;
            alarmTime.setMinutes(alarmTime.getMinutes() - (int) selectedInterval);
            Log.d("CreateAlarmDialog",String.format("Rimossi %3f minuti: ",selectedInterval) + currentTime.toString());
            Log.d("CreateAlarmDialog","Contatore diminuito a: " + timeIterations);
            Log.d("CreateAlarmDialog","sleepHours a: " + sleepHours);
            time(currentTime);
            updateSuggestion();
        });
        root.findViewById(R.id.alarmTimePickerPlus).setOnClickListener(view13 -> {
            timeIterations++;
            sleepHours += sleepHoursModifier;
            alarmTime.setMinutes(alarmTime.getMinutes() + (int) selectedInterval);
            Log.d("CreateAlarmDialog",String.format("Aggiunti %3f minuti: ",selectedInterval) + currentTime.toString());
            Log.d("CreateAlarmDialog","Contatore aumentato a: " + timeIterations);
            Log.d("CreateAlarmDialog","sleepHours a: " + sleepHours);
            time(currentTime);
            updateSuggestion();
        });

        root.findViewById(R.id.alarmConfirm).setOnClickListener(view13 -> {
            EditText titleEditText = root.findViewById(R.id.alarmTimePickerText);
            String alarmTitle;
            alarmTitle = String.valueOf(titleEditText.getText());

            int numberOfSteps = (int) (24 / sleepHoursModifier);
            Log.d("Log in CreateAlarmDialog","numberOfSteps :" + numberOfSteps);
            int offset = timeIterations / numberOfSteps;
            Log.d("Log in CreateAlarmDialog","offset :" + offset);

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

            Log.d("CreateAlarmDialog","Current Time definitivo: " + currentTime.toString());

            NewAlarm newAlarm = new NewAlarm((int) currentTime.getTime(), currentTime,false);
            if (!alarmTitle.equals("")) {
                newAlarm.setTitle(alarmTitle);
            }
            if (selectedMp3Uri != null) {
                newAlarm.setRingtoneUriString(selectedMp3Uri.toString());
            }
            Log.e("Log in CreateAlarmDialog", "Alarm: " + newAlarm.toString());
            listener.onTimeSelected(newAlarm);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedMp3Uri = data.getData();
            if (selectedMp3Uri.getScheme().equals("content")) {
                try (Cursor cursor = getContext().getContentResolver().query(selectedMp3Uri, null, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String ringtoneName = cursor.getString(
                                cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                        ringtoneName = ringtoneName.replace(".mp3","");
                        Toast.makeText(getContext(), getString(R.string.ringtone_selected_is) + "\n" + ringtoneName, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            selectedMp3Uri = null;
        }
    }

}
