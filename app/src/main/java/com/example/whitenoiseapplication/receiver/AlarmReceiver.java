package com.example.whitenoiseapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.service.AlarmService;
import com.example.whitenoiseapplication.service.RescheduleAlarmService;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private Alarm alarm;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startRescheduleAlarmsService(context);
        } else {
            int actionAlarm = intent.getIntExtra("action_alarm", 0);
            if (actionAlarm > 0) {
                Intent actionIntent = new Intent(context, AlarmService.class);
                actionIntent.putExtra("action_alarm_service", actionAlarm);
                context.startService(actionIntent);
            }

            Bundle bundle = intent.getBundleExtra("bundle_alarm");
            if (bundle != null) {
                alarm = (Alarm) bundle.getSerializable("object_alarm");
                if (alarm != null) {
                    if (!alarm.isRecurring() || (alarm.isRecurring() && alarmIsToday()))
                        startAlarmService(context);
                }
            }
        }
    }

    private boolean alarmIsToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                return alarm.isMonday();
            case Calendar.TUESDAY:
                return alarm.isTuesday();
            case Calendar.WEDNESDAY:
                return alarm.isWednesday();
            case Calendar.THURSDAY:
                return alarm.isThursday();
            case Calendar.FRIDAY:
                return alarm.isFriday();
            case Calendar.SATURDAY:
                return alarm.isSaturday();
            case Calendar.SUNDAY:
                return alarm.isSunday();
        }
        return false;
    }

    private void startAlarmService(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_alarm", alarm);
        intentService.putExtra("bundle_alarm", bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
