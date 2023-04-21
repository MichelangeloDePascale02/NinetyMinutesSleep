package com.swdp31plus.ninetyminutessleep.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.swdp31plus.ninetyminutessleep.AlarmService;
import com.swdp31plus.ninetyminutessleep.R;


public class AlarmShowFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm_show, container, false);

        // this listener is used to dismiss an alarm
        rootView.findViewById(R.id.dismissAlarmButton).setOnClickListener(v -> {
            Intent intentService = new Intent(getContext(), AlarmService.class);
            getContext().stopService(intentService);
            requireActivity().finish();
        });
        return rootView;
    }
}
