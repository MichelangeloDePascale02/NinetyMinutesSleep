package com.swdp31plus.ninetyminutessleep.ui.main.pomodoro;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.swdp31plus.ninetyminutessleep.databinding.FragmentPomodoroBinding;
import com.swdp31plus.ninetyminutessleep.services.PomodoroService;
import com.swdp31plus.ninetyminutessleep.ui.main.PageViewModel;
import com.swdp31plus.ninetyminutessleep.utilities.ValuesUtilities;

import java.util.Objects;

public class PomodoroFragment extends Fragment implements PomodoroService.TimerUpdateListener {
    private FragmentPomodoroBinding binding;
    private View rootView;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private PomodoroService pomodoroService;
    private boolean isServiceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PomodoroService.LocalBinder binder = (PomodoroService.LocalBinder) iBinder;
            pomodoroService = binder.getService();
            pomodoroService.setTimerUpdateListener(PomodoroFragment.this);
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };

    public static PomodoroFragment newInstance(int index) {
        PomodoroFragment fragment = new PomodoroFragment();
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
        assert getArguments() != null;
        pageViewModel.setIndex(getArguments().getInt(ARG_SECTION_NUMBER));

        Intent serviceIntent = new Intent(getActivity(), PomodoroService.class);
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isServiceBound) {
            getActivity().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        binding.semiCircleView.setMillis((ValuesUtilities.PomodoroFlags.TOTAL_TIME) * 1000L * 60);
        binding.semiCircleView.setProgress(100);
        rootView = binding.getRoot();
        return rootView;
    }

    @SuppressLint("ServiceCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.startPomodoroButton.setOnClickListener(v -> {
            if (isServiceBound) {
                pomodoroService.startTimer((ValuesUtilities.PomodoroFlags.TOTAL_TIME) * 1000L * 60);
            }
        });

        binding.stopPomodoroButton.setOnClickListener(v -> {
            if (isServiceBound) {
                pomodoroService.stopTimer();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onTimerUpdate(long remainingTime) {
        try {
            binding.semiCircleView.setMillis(remainingTime);
            int remainingTimeMinutes = (int) remainingTime / 60000; // TODO: improve accuracy
            binding.semiCircleView.setProgress((remainingTimeMinutes * 100) / (int) ValuesUtilities.PomodoroFlags.TOTAL_TIME);
        } catch (Exception e) {
            Log.e("PomodoroFragment","Something broke!");
        }
    }
}
