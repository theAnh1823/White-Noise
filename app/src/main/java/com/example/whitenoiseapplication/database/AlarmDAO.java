package com.example.whitenoiseapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.whitenoiseapplication.model.Alarm;

import java.util.List;

@Dao
public interface AlarmDAO {
    @Insert
    void insertAlarm(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAllAlarm();

    @Query("SELECT * FROM alarm_table ORDER BY alarmHour, alarmMinute ASC")
    LiveData<List<Alarm>> getAlarms();

    @Update
    void updateAlarm(Alarm alarm);
}
