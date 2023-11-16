package com.swdp31plus.ninetyminutessleep.ui.fragments.alarms;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.BuildConfig;
import com.swdp31plus.ninetyminutessleep.R;

public class CycleSelectionDialog extends DialogFragment {

    private CycleSelectionDialog.CommunicationInterface listener;
    public interface CommunicationInterface {
        void onCycleSelected(int minutes);
    }


    public void setListener(CycleSelectionDialog.CommunicationInterface listener) {this.listener = listener; }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_number_picker, null);
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
        titleText.setText(getString(R.string.set_cycle_duration));
        titleText.setTextSize(22);
        builder.setCustomTitle(titleView);

        SharedPreferences preferences = getContext().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
        int selectedInterval = preferences.getInt("selectedInterval",90);

        ((TextView) root.findViewById(R.id.button_dialog_current_timeout)).setText(String.format(getString(R.string.current_cycle_duration), selectedInterval));

        NumberPicker numberPicker = root.findViewById(R.id.numberPicker);

        String minutesValues[];

        if (BuildConfig.DEBUG) {
            minutesValues = new String[]{"1", "5", "15", "30", "45", "60", "90", "120"};
            numberPicker.setMaxValue(minutesValues.length - 1);
            numberPicker.setMinValue(0);
            numberPicker.setValue(6);
        } else {
            minutesValues = new String[]{"15", "30", "45", "60", "90", "120"};
            numberPicker.setMaxValue(minutesValues.length - 1);
            numberPicker.setMinValue(0);
            numberPicker.setValue(4);
        }

        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(minutesValues);

        /*((SeekBar) root.findViewById(R.id.seek_bar_number_timeout)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((TextView) root.findViewById(R.id.text_view_number_timeout)).setText(String.format("%d %s", i, getString(R.string.minutes)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });*/

        final AlertDialog dialog = builder.create();

        root.findViewById(R.id.button_dialog_set_timeout).setOnClickListener(v -> {
            dialog.dismiss();
            //listener.onCycleSelected(((SeekBar) root.findViewById(R.id.seek_bar_number_timeout)).getProgress());
            listener.onCycleSelected(Integer.parseInt(minutesValues[numberPicker.getValue()]));
        });

        dialog.show();

        return dialog;
    }
}
