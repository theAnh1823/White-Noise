package com.example.whitenoiseapplication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ActivityAlarmAlertBinding;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.service.AlarmService;
import com.example.whitenoiseapplication.util.LocaleHelper;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import at.markushi.ui.CircleButton;

public class AlarmAlertActivity extends AppCompatActivity {
    private Alarm alarm;

    @SuppressLint({"DefaultLocale", "ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.whitenoiseapplication.databinding.ActivityAlarmAlertBinding binding = ActivityAlarmAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        Context context;
        if (isVietnameseLanguage) {
            context = LocaleHelper.setLocale(this, "vi");
        } else {
            context = LocaleHelper.setLocale(this, "en");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        Bundle bundle = getIntent().getBundleExtra("bundle_alarm");
        if (bundle != null) {
            alarm = (Alarm) bundle.getSerializable("object_alarm");
        }

        Calendar calendar = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        String currentDate = new SimpleDateFormat("EEEE, dd/MM/yyyy", locale).format(calendar.getTime());
        binding.tvCurrentTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        binding.tvCurrentDate.setText(currentDate);
        String titleAlarm = (alarm.getTitleAlarm().isEmpty()) ? getString(R.string.alarm) : alarm.getTitleAlarm();
        binding.tvTitleAlarm.setText(titleAlarm);

        binding.btnSnooze10Mins.setText(R.string.snooze_10_mins);
        binding.btnSnooze10Mins.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.snooze_10_mins), Toast.LENGTH_LONG).show();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MINUTE, 10);

            Alarm alarm = new Alarm(
                    new Random().nextInt(Integer.MAX_VALUE),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    R.string.once,
                    this.alarm.getTitleAlarm(),
                    this.alarm.getRingtoneId(),
                    this.alarm.getRingToneAlarm(),
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            );

            alarm.schedule(getApplicationContext());

            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        binding.btnDisableAlarm.setOnClickListener(v -> {
            if (!alarm.isRecurring()){
                alarm.cancelAlarm(this);
                new ViewModelProvider(this).get(AlarmsListViewModel.class).update(alarm);
            }
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });
    }
}
