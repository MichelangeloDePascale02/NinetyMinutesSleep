package com.swdp31plus.ninetyminutessleep;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.services.AlarmReceiver;
import com.swdp31plus.ninetyminutessleep.ui.main.SectionsPagerAdapter;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;

import java.io.Serializable;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int timeoutTimerInMillis = 0;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

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
            if (item.getItemId() == R.id.killswitch) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getString(R.string.killswitch_title));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);

                TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
                textView.setText(getString(R.string.killswitch_text));

                builder.setView(dialogView);

                CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
                checkBox.setVisibility(View.GONE);

                Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

                final AlertDialog dialog = builder.create();

                closeBtn.setOnClickListener(v -> {
                    dialog.dismiss();

                    NewAlarm currentAlarm = (NewAlarm) StorageUtilities.loadObject("currentAlarm.obj", MainActivity.this);
                    if (currentAlarm != null) {
                        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        intent.putExtra("alarm", (Parcelable) currentAlarm);

                        Log.e("Log in alarmservice", "Informazioni allarme");
                        Log.e("Log in alarmservice", "" + currentAlarm.getId());
                        Log.e("Log in alarmservice", currentAlarm.getTime().toString());
                        Log.e("Log in alarmservice", currentAlarm.toString());

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                getApplicationContext(),
                                currentAlarm.getId(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        StorageUtilities.saveAlarm((Serializable) null,"currentAlarm.obj",getApplicationContext());
                    }

                    MainActivity.this.finish();
                });

                dialog.show();
            }
            return false;
        });

        Log.d("Manufacturer", Build.MANUFACTURER.toLowerCase(Locale.ROOT));

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
        }

        binding.topAppBar.setNavigationOnClickListener(view -> {});

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (!Settings.canDrawOverlays(this)) {
            } else {
                Log.e("mainactivity", "permission granted");
            }
        }
    }

    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + this.getPackageName()));
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }
}