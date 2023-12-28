package com.swdp31plus.ninetyminutessleep.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.swdp31plus.ninetyminutessleep.BuildConfig;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;
import com.swdp31plus.ninetyminutessleep.utilities.NetworkUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


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

        binding.viewPager.setOffscreenPageLimit(3);

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(binding.viewPager);

        if (!Settings.canDrawOverlays(this) || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_tutorial_information,null);

            // Set dialog title
            View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(getString(R.string.permissions_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            TextView textView = dialogView.findViewById(R.id.text_view_dialog_tutorial_information);
            textView.setText(getString(R.string.permissions_text));

            builder.setView(dialogView);

            CheckBox checkBox = dialogView.findViewById(R.id.check_box_dialog_tutorial_information);
            checkBox.setVisibility(View.GONE);

            Button closeBtn = dialogView.findViewById(R.id.button_dialog_tutorial_information);

            final AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(false);

            closeBtn.setOnClickListener(v -> {
                if (isMIUI()) {
                    try {
                        Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                        localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                        localIntent.putExtra("extra_pkgname", getPackageName());
                        startActivityForResult(localIntent, 123);
                    } catch (Exception ignore) {}
                }

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName());
                    startActivityForResult(intent, 124);
                }

                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 123);
                }
                dialog.dismiss();
            });
            dialog.show();
        }

        binding.floatingActionButtonGlobal.setVisibility(View.GONE);

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.about) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_about,null);
                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getApplicationContext().getString(R.string.about_title));
                TextView textView = dialogView.findViewById(R.id.text_view_dialog_about);

                String aboutText = new StringBuilder()
                        .append(getString(R.string.about_text))
                        .append("\n\nVersion info: ")
                        .append(BuildConfig.VERSION_NAME)
                        .toString();

                if(BuildConfig.DEBUG) {
                    aboutText = new StringBuilder()
                            .append(aboutText)
                            .append(" " + getString(R.string.app_version_name))
                            .append(" - DEBUG")
                            .toString();
                } else {
                    aboutText = new StringBuilder()
                            .append(aboutText)
                            .append(" " + getString(R.string.app_version_name))
                            .append(" - RELEASE")
                            .toString();
                }

                textView.setText(aboutText);
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.button_dialog_about_dismiss).setOnClickListener(v -> {
                    dialog.dismiss();
                });

                dialogView.findViewById(R.id.button_dialog_about_check_version).setOnClickListener(v -> {
                    NetworkUtilities.GitHubReleaseTask gitHubReleaseTask = new NetworkUtilities.GitHubReleaseTask();
                    gitHubReleaseTask.setContext(getApplicationContext());
                    gitHubReleaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_numerical_slider_120,null);
                // Set dialog title
                View titleView = getLayoutInflater().inflate(R.layout.dialog_generic_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
                titleText.setText(getApplicationContext().getString(R.string.timeout_title));
                titleText.setTextSize(22);
                builder.setCustomTitle(titleView);
                builder.setView(dialogView);

                ((SeekBar) dialogView.findViewById(R.id.seek_bar_number_timeout)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        ((TextView) dialogView.findViewById(R.id.text_view_number_timeout)).setText(String.format("%d %s", i, getString(R.string.minutes)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                final AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.button_dialog_set_timeout).setOnClickListener(v -> {
                    timeoutTimerInMillis = ((SeekBar) dialogView.findViewById(R.id.seek_bar_number_timeout)).getProgress();
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
            if (item.getItemId() == R.id.bug) {
                String phoneInfo ="Debug-infos:";
                phoneInfo += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
                phoneInfo += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
                phoneInfo += "\n Device: " + android.os.Build.DEVICE;
                phoneInfo += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ninetyminutessleep90@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_found_mail));
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_TEXT, phoneInfo + "\n\n" + "---Please do not delete the text before---\n\n");
                emailIntent.setSelector(emailSelectorIntent);

                startActivity(emailIntent);
            }
            return false;
        });

        binding.topAppBar.setNavigationOnClickListener(view -> {});

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    binding.floatingActionButtonGlobal.setVisibility(View.VISIBLE);
                } else {
                    binding.floatingActionButtonGlobal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (!Settings.canDrawOverlays(this)) {
                MainActivity.this.finish();
            } else {
                Log.e("Log in MainActivity", "Full Permission Granted, app will work.");
            }
        }
    }

    public static boolean isMIUI() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public FloatingActionButton getGlobalFAB() {
        return binding.floatingActionButtonGlobal;
    }

}