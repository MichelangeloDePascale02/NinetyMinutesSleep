package com.swdp31plus.ninetyminutessleep.ui.main.pomodoro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.FragmentPomodoroBinding;
import com.swdp31plus.ninetyminutessleep.ui.customviews.SemiCircleView;
import com.swdp31plus.ninetyminutessleep.ui.main.PageViewModel;

public class PomodoroFragment extends Fragment {
    private FragmentPomodoroBinding binding;
    private View rootView;
    private PageViewModel pageViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        binding.semiCircleView.setProgress(100);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.startPomodoroButton.setOnClickListener(v -> {
            for(int i = 0; i <= 100; i++) {
                Log.w("CIAO","" + i);
                binding.semiCircleView.setProgress(i);
            }
        });
    }
}
