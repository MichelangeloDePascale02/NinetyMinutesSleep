package com.swdp31plus.ninetyminutessleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.swdp31plus.ninetyminutessleep.ui.main.SectionsPagerAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;
import com.swdp31plus.ninetyminutessleep.ui.main.SoundFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private int timeoutTimerInMillis = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        navController = Objects.requireNonNull(navHostFragment).getNavController();

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);


        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_first:
                    navController.navigate(R.id.firstFragment);
                    return true;
                case R.id.action_second:
                    navController.navigate(R.id.secondFragment);
                    return true;
            }
            return false;
        });

        NavigationUI.setupActionBarWithNavController(this, navController);

        // warning dialog building
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("show_warning", true)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            SharedPreferences.Editor editor = preferences.edit();

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(getString(R.string.warning_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
            textView.setText(getString(R.string.warning_text));

            builder.setView(dialogView);

            CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                editor.putBoolean("show_warning", !isChecked);
                editor.apply();
            });

            Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

            final AlertDialog dialog = builder.create();

            closeBtn.setOnClickListener(v -> {
                editor.apply();
                dialog.dismiss();
            });

            dialog.show();
        }

        /*sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setVisibility(View.GONE);*/



        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.about) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_about,null);
                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getApplicationContext().getString(R.string.about_title));
                TextView textView = dialogView.findViewById(R.id.text_view_dialog_about);
                textView.setText(getString(R.string.about_text));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.button_dialog_about_dismiss).setOnClickListener(v -> {
                    dialog.dismiss();
                });

                dialogView.findViewById(R.id.button_dialog_about_github).setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.github_link)));
                    startActivity(i);
                });

                dialog.show();
                return true;
            } else if (item.getItemId() == R.id.sleep_timer_for_sounds) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_sound_timeout,null);
                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getApplicationContext().getString(R.string.timeout_title));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);
                builder.setView(dialogView);

                ((SeekBar) dialogView.findViewById(R.id.seek_bar_sound_timeout)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        ((TextView) dialogView.findViewById(R.id.text_view_sound_timeout)).setText(String.format("%d %s", i, getString(R.string.minutes)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                final AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.button_dialog_sound_timeout).setOnClickListener(v -> {
                    timeoutTimerInMillis = ((SeekBar) dialogView.findViewById(R.id.seek_bar_sound_timeout)).getProgress();
                    dialog.dismiss();
                    ((SoundFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.firstFragment))).onTimeOutSet(timeoutTimerInMillis);
                });
                dialog.show();
                return true;
            } else if (item.getItemId() == R.id.why90) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getString(R.string.explanation_90_title));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);

                TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
                textView.setText(getString(R.string.explanation_90_text));

                builder.setView(dialogView);

                CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
                checkBox.setVisibility(View.GONE);

                Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

                final AlertDialog dialog = builder.create();

                closeBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                });

                dialog.show();
            }
            return false;
        });

        binding.topAppBar.setNavigationOnClickListener(view -> {});

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}});*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

}