package com.swdp31plus.ninetyminutessleep.ui.main.pomodoro;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentPomodoroBinding;
import com.swdp31plus.ninetyminutessleep.services.PomodoroService;
import com.swdp31plus.ninetyminutessleep.ui.main.MainActivity;
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
    private SharedPreferences sharedPreferences;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection);
            isServiceBound = false;
        }

        if (pomodoroService != null && pomodoroService.getRemainingTime() > 0) {
            requireActivity().startService(new Intent(getActivity(), PomodoroService.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(ValuesUtilities.PomodoroFlags.POMODORO_PREFERENCES, Context.MODE_PRIVATE);
        long durationTime = sharedPreferences.getLong(ValuesUtilities.PomodoroFlags.DURATION_TIME, 25);
        binding.semiCircleView.setMillis(durationTime * 1000L * 60);
        binding.semiCircleView.setProgress(100);

        rootView = binding.getRoot();
        return rootView;
    }

    @SuppressLint("ServiceCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.stopPomodoroButton.setVisibility(View.GONE);

        binding.startPomodoroButton.setOnClickListener(v -> {
            startTimer();
        });

        binding.stopPomodoroButton.setOnClickListener(v -> {
            abortTimer();
        });

        binding.durationPomodoroButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_numerical_slider,null);
            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(requireContext().getString(R.string.timeout_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);
            builder.setView(dialogView);

            ((TextView) dialogView.findViewById(R.id.text_view_number_timeout)).setText(getString(R.string.reset_on_duration_pomodoro));

            ((SeekBar) dialogView.findViewById(R.id.seek_bar_number_timeout)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    i /= 2; // Trying to put 60 minutes as maximum
                    ((TextView) dialogView.findViewById(R.id.text_view_number_timeout)).setText(
                            String.format("%d %s\n%s", i, getString(R.string.minutes), getString(R.string.reset_on_duration_pomodoro)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            final AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.button_dialog_set_timeout).setOnClickListener(v2 -> {
                dialog.dismiss();
                abortTimer();

                sharedPreferences = requireActivity().getSharedPreferences(ValuesUtilities.PomodoroFlags.POMODORO_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                long durationTime = ((SeekBar) dialogView.findViewById(R.id.seek_bar_number_timeout)).getProgress() / 2;
                if (durationTime == 0)
                    durationTime = 1;
                editor.putLong(ValuesUtilities.PomodoroFlags.DURATION_TIME, durationTime);
                editor.apply();

                binding.semiCircleView.setMillis(durationTime * 1000L * 60);
                binding.semiCircleView.setProgress(100);
            });
            dialog.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(getActivity(), PomodoroService.class);
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        sharedPreferences = requireActivity().getSharedPreferences(ValuesUtilities.PomodoroFlags.POMODORO_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(ValuesUtilities.PomodoroFlags.TIMER_IS_SET, false)) {  // Get saved value.
            binding.stopPomodoroButton.setVisibility(View.VISIBLE);                                     // If it doesn't exists, then it is assert it is false.
            binding.startPomodoroButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onTimerUpdate(long remainingTime) {
        try {
            binding.semiCircleView.setMillis(remainingTime);
            int progress = (int) (((remainingTime * 100) / (int) ValuesUtilities.PomodoroFlags.DEFAULT_TIME) / 60000);
            binding.semiCircleView.setProgress(progress);
        } catch (Exception e) {
            Log.e("PomodoroFragment","Something regarding the timer broke.");
            Toast.makeText(getContext(),getString(R.string.something_broke), Toast.LENGTH_LONG).show();
        }
    }

    private void startTimer() {
        sharedPreferences = requireActivity().getSharedPreferences(ValuesUtilities.PomodoroFlags.POMODORO_PREFERENCES, Context.MODE_PRIVATE);
        long durationTime = sharedPreferences.getLong(ValuesUtilities.PomodoroFlags.DURATION_TIME, 25);

        Intent serviceIntent = new Intent(getActivity(), PomodoroService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (isServiceBound) {
            pomodoroService.startTimer(durationTime * 1000L * 60);
            binding.stopPomodoroButton.setVisibility(View.VISIBLE);
            binding.startPomodoroButton.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ValuesUtilities.PomodoroFlags.TIMER_IS_SET, true);
            editor.apply();
        }
    }

    private void abortTimer() {
        if (isServiceBound) {
            pomodoroService.stopTimer();
            binding.stopPomodoroButton.setVisibility(View.GONE);
            binding.startPomodoroButton.setVisibility(View.VISIBLE);
            sharedPreferences = requireActivity().getSharedPreferences(ValuesUtilities.PomodoroFlags.POMODORO_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ValuesUtilities.PomodoroFlags.TIMER_IS_SET, false);
            editor.apply();
        }
    }
}
