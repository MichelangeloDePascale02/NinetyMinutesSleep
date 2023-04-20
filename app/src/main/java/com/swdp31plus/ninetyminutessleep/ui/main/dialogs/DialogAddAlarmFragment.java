package com.swdp31plus.ninetyminutessleep.ui.main.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class DialogAddAlarmFragment extends DialogFragment {

    public interface GetData {
        void onDialogDismissed(Alarm alarm);
    }

    private GetData getDataListener;

    public DialogAddAlarmFragment() {
    }

    public void setListener(GetData listener) {
        this.getDataListener = listener;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_alarm_set, null);

        // Set dialog main options
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Nuova sveglia");
        builder.setCustomTitle(titleView);

        Button btnConfirmAnimalRegistration = root.findViewById(R.id.alarmConfirm);

        btnConfirmAnimalRegistration.setOnClickListener(v -> {
            String time = "";
            //getDataListener.onDialogDismissed(new Alarm(time));
            dismiss();
        });

        return builder.create();
    }
}
