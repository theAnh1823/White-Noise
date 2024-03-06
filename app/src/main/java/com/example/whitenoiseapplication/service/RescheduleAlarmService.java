package com.example.whitenoiseapplication.service;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.repository.AlarmRepository;

import java.util.List;

public class RescheduleAlarmService extends LifecycleService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        alarmRepository.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                for (Alarm alarm : alarms){
                    if (alarm.isAlarmEnabled())
                        alarm.schedule(getApplicationContext());
                }
            }
        });
        return START_STICKY;
    }

}
