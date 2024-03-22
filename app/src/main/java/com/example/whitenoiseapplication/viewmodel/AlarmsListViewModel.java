package com.example.whitenoiseapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.repository.AlarmRepository;

import java.util.List;

public class AlarmsListViewModel extends AndroidViewModel {
    private final AlarmRepository alarmRepository;
    private final LiveData<List<Alarm>> listLiveData;

    public AlarmsListViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
        listLiveData = alarmRepository.getAlarmsLiveData();
    }

    public void insert(Alarm alarm){
        alarmRepository.insert(alarm);
    }

    public void update(Alarm alarm){
        alarmRepository.update(alarm);
    }

    public void delete(Alarm alarm){
        alarmRepository.delete(alarm);
    }

    public LiveData<List<Alarm>> getListLiveData() {
        return listLiveData;
    }
}
