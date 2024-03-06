package com.example.whitenoiseapplication.database;

import static com.example.whitenoiseapplication.database.AlarmDatabase.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.whitenoiseapplication.model.Alarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Alarm.class}, version = DATABASE_VERSION)
public abstract class AlarmDatabase extends RoomDatabase {
    private static AlarmDatabase alarmDatabase;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarm-database";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract AlarmDAO alarmDAO();

    public static AlarmDatabase getInstance(Context context) {
        if (alarmDatabase == null) {
            alarmDatabase = Room.databaseBuilder(context.getApplicationContext(), AlarmDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return alarmDatabase;
    }
}


