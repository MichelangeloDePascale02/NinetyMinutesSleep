package com.swdp31plus.ninetyminutessleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.swdp31plus.ninetyminutessleep.databinding.ActivityAlarmBinding;

public class AlarmActivity extends AppCompatActivity {

    private ActivityAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this activity is launched when the user clicks on the notification for the alarm
        super.onCreate(savedInstanceState);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // this listener is used to dismiss an alarm
        binding.dismissAlarmButton.setOnClickListener(v -> {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });
    }
}
