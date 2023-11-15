package com.swdp31plus.ninetyminutessleep.ui.main.alarms;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentNewAlarmBinding;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.services.AlarmService;
import com.swdp31plus.ninetyminutessleep.ui.fragments.alarms.CreateAlarmDialog;
import com.swdp31plus.ninetyminutessleep.ui.fragments.alarms.CycleSelectionDialog;
import com.swdp31plus.ninetyminutessleep.ui.main.MainActivity;
import com.swdp31plus.ninetyminutessleep.ui.main.PageViewModel;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;
import com.swdp31plus.ninetyminutessleep.utilities.ValuesUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AlarmFragment extends Fragment implements
        CreateAlarmDialog.CommunicationInterface,
        CycleSelectionDialog.CommunicationInterface {
    private FragmentNewAlarmBinding binding;
    private PageViewModel pageViewModel;
    private View rootView;
    private AlarmsAdapter alarmsAdapter;
    private ArrayList<NewAlarm> readFromFile;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static AlarmFragment newInstance(int index) {
        AlarmFragment fragment = new AlarmFragment();
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
        alarmsAdapter = new AlarmsAdapter();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentNewAlarmBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readFromFile = (ArrayList<NewAlarm>) StorageUtilities.loadAlarms("currentAlarm.obj", requireContext());

        if (readFromFile != null)
        {
            alarmsAdapter.addAll(readFromFile);
            for (NewAlarm alarm : readFromFile) {
                Log.e("NewAlarmDialog","alarm# : " + alarm.toString());
            }
        }

        binding.alarmListLiterallyRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        binding.alarmListToDialogNewAlarmButton.setOnClickListener(v -> {
            CreateAlarmDialog dataPickerForAlarmDialog = new CreateAlarmDialog();
            dataPickerForAlarmDialog.setListener(this);
            dataPickerForAlarmDialog.show(getParentFragmentManager(), "dataPickerForAlarmDialog");
        });

        alarmsAdapter.setOnItemClickListener(alarm -> {
            MaterialAlertDialogBuilder confirmDialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Title")
                    .setMessage("Do you really want to whatever?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("YES", (dialog, which) -> {
                        prepareAlarmToStartService(alarm, ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_DISMISS);
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            confirmDialogBuilder.create().show();
        });
        binding.alarmListLiterallyRV.setAdapter(alarmsAdapter);

        binding.intervalSelectorButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.coming_soon), Toast.LENGTH_LONG).show();
            // Method is ready, but untested. Lacking modifications in CreateAlarmDialog
            /*CycleSelectionDialog cycleSelectionDialog = new CycleSelectionDialog();
            cycleSelectionDialog.setListener(AlarmFragment.this);
            cycleSelectionDialog.show(getParentFragmentManager(), "CycleSelectionDialog");*/
        });

        binding.ringtoneSelectorButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, 1);

            /*RingtoneManager ringtoneManager = new RingtoneManager(getContext());
            ringtoneManager.setType(RingtoneManager.TYPE_ALL);

            Cursor cursor = ringtoneManager.getCursor();

            final ArrayList<String> ringtoneNames = new ArrayList<>();
            final ArrayList<Uri> ringtoneUris = new ArrayList<>();

            while (cursor.moveToNext()) {
                ringtoneNames.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                ringtoneUris.add(ringtoneManager.getRingtoneUri(cursor.getPosition()));
            }

            final String[] items = ringtoneNames.toArray(new String[0]);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(getContext().getString(R.string.ringtone_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            builder.setItems(items, (dialog, item) -> {
                String selectedRingtoneName = ringtoneNames.get(item);
                Uri selectedRingtoneUri = ringtoneUris.get(item);

                SharedPreferences preferences = getActivity().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedRingtoneName", selectedRingtoneName);
                editor.putString("selectedRingtoneUri", selectedRingtoneUri.toString());
                editor.apply();

                dialog.dismiss();
            });

            builder.show();*/
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedMp3Uri = data.getData();

             SharedPreferences preferences = getActivity().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
             SharedPreferences.Editor editor = preferences.edit();
             editor.putString("selectedMp3Uri", selectedMp3Uri.toString());
             editor.apply();
        }
    }

    @Override
    public void onTimeSelected(Date alarmDate) {
        // (int) alarmDate.getTime() is not a good way to create an ID, because it creates an overflow
        NewAlarm newAlarm = new NewAlarm((int) alarmDate.getTime(), alarmDate,false);
        prepareAlarmToStartService(newAlarm, ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_SCHEDULE);
    }

    @Override
    public void onCycleSelected(int minutes) {
        SharedPreferences preferences = getActivity().getSharedPreferences("NinetyMinutesSleepPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selectedInterval", minutes);
        editor.apply();
        Log.w("Log in AlarmFragment","Ciclo impostato di: " + minutes);
    }

    private void prepareAlarmToStartService(NewAlarm alarm, String action) {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra("alarm", (Parcelable) alarm);
        intent.putExtra("action", action);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(requireActivity().getPackageName()));
            startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        Log.d("Log in AlarmFragment", "La action è: " + action);
        Log.d("Log in AlarmFragment", "L'allarme al suo interno ha: " + alarm.toString());
        if (action.equals(ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_SCHEDULE)) {
            alarmsAdapter.add(alarm);
        } else if (action.equals(ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_DISMISS)) {
            alarmsAdapter.remove(alarm);
        }
        alarmsAdapter.notifyDataSetChanged();
        StorageUtilities.saveAlarms(alarmsAdapter.getAlarmsList(),"currentAlarm.obj", requireContext());

        requireActivity().startService(intent);
    }

    @Override
    public void onResume() {
        readFromFile = (ArrayList<NewAlarm>) StorageUtilities.loadAlarms("currentAlarm.obj", requireContext());

        if (readFromFile != null)
        {
            alarmsAdapter.removeAll();
            alarmsAdapter.addAll(readFromFile);
            for (NewAlarm alarm : readFromFile) {
                Log.e("NewAlarmDialog","alarm# : " + alarm.toString());
            }
            alarmsAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}
