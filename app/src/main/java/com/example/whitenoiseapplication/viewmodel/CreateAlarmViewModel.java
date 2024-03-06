package com.example.whitenoiseapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.repository.AlarmRepository;

public class CreateAlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateAlarmViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm){
        alarmRepository.insert(alarm);
    }
}
