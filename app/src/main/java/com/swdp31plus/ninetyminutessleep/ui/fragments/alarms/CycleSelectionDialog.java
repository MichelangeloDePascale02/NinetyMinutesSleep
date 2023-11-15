package com.swdp31plus.ninetyminutessleep.ui.fragments.alarms;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
        View root = inflater.inflate(R.layout.dialog_numerical_slider_120, null);
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
        titleText.setText(getString(R.string.set_cycle_duration));
        titleText.setTextSize(22);
        builder.setCustomTitle(titleView);

        ((SeekBar) root.findViewById(R.id.seek_bar_number_timeout)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((TextView) root.findViewById(R.id.text_view_number_timeout)).setText(String.format("%d %s", i, getString(R.string.minutes)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final AlertDialog dialog = builder.create();

        root.findViewById(R.id.button_dialog_set_timeout).setOnClickListener(v -> {
            dialog.dismiss();
            listener.onCycleSelected(((SeekBar) root.findViewById(R.id.seek_bar_number_timeout)).getProgress());
        });

        dialog.show();

        return dialog;
    }
}
