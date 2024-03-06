package com.example.whitenoiseapplication.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.whitenoiseapplication.R;

public class MainApplication extends Application {
    public static final String CHANNEL_ID_ALARM = "CHANNEL_ALARM_SERVICE";
    public static final String CHANNEL_ID_AUDIO = "CHANNEL_AUDIO_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importanceAudio = NotificationManager.IMPORTANCE_DEFAULT;
            int importanceAlarm = NotificationManager.IMPORTANCE_HIGH;

            CharSequence sequenceAlarm = getString(R.string.channel_alarm_service_name);
            NotificationChannel channelAlarm = new NotificationChannel(CHANNEL_ID_ALARM, sequenceAlarm, importanceAlarm);
            channelAlarm.setSound(null, null);
            NotificationManager notificationManagerAlarm = getSystemService(NotificationManager.class);
            if(notificationManagerAlarm != null){
                notificationManagerAlarm.createNotificationChannel(channelAlarm);
            }

            CharSequence sequenceAudio = getString(R.string.channel_audio_service_name);
            NotificationChannel channelAudio = new NotificationChannel(CHANNEL_ID_AUDIO, sequenceAudio, importanceAudio);
            channelAudio.setSound(null, null);
            NotificationManager notificationManagerAudio = getSystemService(NotificationManager.class);
            if(notificationManagerAudio != null){
                notificationManagerAudio.createNotificationChannel(channelAudio);
            }
        }
    }
}
