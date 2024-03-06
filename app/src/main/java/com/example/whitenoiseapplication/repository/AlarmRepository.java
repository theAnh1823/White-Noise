package com.example.whitenoiseapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.whitenoiseapplication.database.AlarmDAO;
import com.example.whitenoiseapplication.database.AlarmDatabase;
import com.example.whitenoiseapplication.model.Alarm;

import java.util.List;

public class AlarmRepository {
    private AlarmDAO alarmDao;
    private LiveData<List<Alarm>> alarmsLiveData;
    public AlarmRepository(Application application) {
        AlarmDatabase alarmDatabase = AlarmDatabase.getInstance(application);
        alarmDao = alarmDatabase.alarmDAO();
        alarmsLiveData = alarmDao.getAlarms();
    }

    public void insert(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.insertAlarm(alarm);
        });
    }

    public void update(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.updateAlarm(alarm);
        });
    }

    public void delete(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.deleteAlarm(alarm);
        });
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}
