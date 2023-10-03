package com.swdp31plus.ninetyminutessleep.ui.main.alarms;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.adapters.AlarmsAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentNewAlarmBinding;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.services.AlarmService;
import com.swdp31plus.ninetyminutessleep.ui.fragments.alarms.CreateAlarmDialog;
import com.swdp31plus.ninetyminutessleep.ui.main.PageViewModel;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;
import com.swdp31plus.ninetyminutessleep.utilities.ValuesUtilities;

import java.util.ArrayList;
import java.util.Date;

public class AlarmFragment extends Fragment implements CreateAlarmDialog.CommunicationInterface {
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
                        iPromiseItWillBeGone(alarm, ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_DISMISS);
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            confirmDialogBuilder.create().show();
        });
        binding.alarmListLiterallyRV.setAdapter(alarmsAdapter);
    }

    //TODO: refactor to get a NewAlarm object in input and to generalize the method
    @Override
    public void onTimeSelected(Date alarmDate) {
        NewAlarm newAlarm = new NewAlarm((int) alarmDate.getTime(), alarmDate,false);
        iPromiseItWillBeGone(newAlarm, ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_SCHEDULE);
    }

    private void iPromiseItWillBeGone(NewAlarm alarm, String action) {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra("alarm", (Parcelable) alarm);
        intent.putExtra("action", action);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(requireActivity().getPackageName()));
            startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        if (action.equals(ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_SCHEDULE)) {
            alarmsAdapter.add(alarm);
        } else if (action.equals(ValuesUtilities.AlarmsStorageFlags.ALARM_ACTION_DISMISS)) {
            alarmsAdapter.remove(alarm);
        } else {

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
