package com.swdp31plus.ninetyminutessleep.ui.main.alarms;

import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NewAlarmFragment extends Fragment implements CreateAlarmDialog.CommunicationInterface {
    private FragmentNewAlarmBinding binding;
    private PageViewModel pageViewModel;
    private View rootView;
    private AlarmsAdapter alarmsAdapter;
    private ArrayList<NewAlarm> readFromFile;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static NewAlarmFragment newInstance(int index) {
        NewAlarmFragment fragment = new NewAlarmFragment();
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
                        iPromiseItWillBeGone(alarm);
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
        // Intent for AlarmService. Inside, there will be the alarm object
        Intent intent = new Intent(getActivity(), AlarmService.class);
        NewAlarm newAlarm = new NewAlarm((int) alarmDate.getTime(), alarmDate,false);
        intent.putExtra("alarm", (Parcelable) newAlarm);
        intent.putExtra("action","schedule");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(requireActivity().getPackageName()));
            startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        alarmsAdapter.add(newAlarm);
        alarmsAdapter.notifyDataSetChanged();
        StorageUtilities.saveAlarms(alarmsAdapter.getAlarmsList(),"currentAlarm.obj", requireContext());

        requireActivity().startService(intent);
    }

    private void iPromiseItWillBeGone(NewAlarm alarm) {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra("alarm", (Parcelable) alarm);
        intent.putExtra("action","dismiss");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getContext())) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(requireActivity().getPackageName()));
            startActivityForResult(permissionIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        alarmsAdapter.remove(alarm);
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
