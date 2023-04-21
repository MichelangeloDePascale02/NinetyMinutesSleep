package com.swdp31plus.ninetyminutessleep.ui.main.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;

import java.util.Date;

public class DialogAddAlarmFragment extends DialogFragment {

    public interface GetData {
        void onDialogDismissed(String hour, String minute);
    }

    private GetData getDataListener;

    private TextView hoursTextView;
    private TextView minutesTextView;
    private int currentHour;
    private int currentMinute;

    public DialogAddAlarmFragment() {
    }

    public void setListener(GetData listener) {
        this.getDataListener = listener;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_alarm_set, null);

        // Set dialog main options
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Nuova sveglia");
        builder.setCustomTitle(titleView);

        Date currentDate = new Date();

        hoursTextView = root.findViewById(R.id.hours_picker);
        minutesTextView = root.findViewById(R.id.minutes_picker);

        hoursTextView.setText(""+currentDate.getHours());
        minutesTextView.setText(""+currentDate.getMinutes());

        Button alarmTimePickerMinus = root.findViewById(R.id.alarmTimePickerMinus);

        alarmTimePickerMinus.setOnClickListener(v -> {
            editCycle(0);
        });

        Button alarmTimePickerPlus = root.findViewById(R.id.alarmTimePickerPlus);

        alarmTimePickerPlus.setOnClickListener(v -> {
            editCycle(1);
        });

        Button alarmConfirm = root.findViewById(R.id.alarmConfirm);

        alarmConfirm.setOnClickListener(v -> {
            getDataListener.onDialogDismissed(prepareText(currentHour), prepareText(currentMinute));
            dismiss();
        });

        return builder.create();
    }

    // 0 to decrease, 1 to increase
    private void editCycle(int param) {
        currentHour = Integer.parseInt((String) hoursTextView.getText());
        currentMinute = Integer.parseInt((String) minutesTextView.getText());

        if (param == 0) {
            subtractHour();
            subtractMinute();
        } else if (param == 1) {
            addHour();
            addMinute();
        }

        hoursTextView.setText(prepareText(currentHour));
        minutesTextView.setText(prepareText(currentMinute));
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
