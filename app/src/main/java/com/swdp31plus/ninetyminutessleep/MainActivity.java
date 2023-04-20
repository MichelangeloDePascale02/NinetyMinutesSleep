package com.swdp31plus.ninetyminutessleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.swdp31plus.ninetyminutessleep.ui.main.SectionsPagerAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int timeoutTimerInMillis = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestPermissions();

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setVisibility(View.GONE);

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
                    sectionsPagerAdapter.getFirstFragment().onTimeOutSet(timeoutTimerInMillis);
                });
                dialog.show();
                return true;
            } else if (item.getItemId() == R.id.why_90) {
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
                return true;
            }
            return false;
        });

        binding.topAppBar.setNavigationOnClickListener(view -> {});

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            public void onPageScrollStateChanged(int state) {}});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public int getTimeoutTimerInMillis() {
        return timeoutTimerInMillis;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "akchannel";
            String desc = "Channel for 90";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("id", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS);
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Log.e("TAG","Granted");
        } else {
            Log.e("TAG","Not Granted");
        }
    });
}