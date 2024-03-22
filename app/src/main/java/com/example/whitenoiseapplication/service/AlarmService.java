package com.example.whitenoiseapplication.service;


import static com.example.whitenoiseapplication.application.MainApplication.CHANNEL_ID_ALARM;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.activity.AlarmAlertActivity;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.receiver.AlarmReceiver;
import com.example.whitenoiseapplication.util.LocaleHelper;

public class AlarmService extends Service {
    public static final int ACTION_CANCEL = 1;
    private Alarm mAlarm;
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    @Override
    public void onCreate() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        if (isVietnameseLanguage) {
            LocaleHelper.setLocale(this, "vi");
        } else {
            LocaleHelper.setLocale(this, "en");
        }

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getBundleExtra("bundle_alarm");
        if (bundle != null) {
            mAlarm = (Alarm) bundle.getSerializable("object_alarm");
        }

        int actionAlarm = intent.getIntExtra("action_alarm_service", 0);
        if (actionAlarm == ACTION_CANCEL){
            stopSelf();
            mAlarm.cancelAlarm(this);
            mMediaPlayer.stop();
        }

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), mAlarm.getRingToneAlarm());
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        long[] pattern = {0, 300, 200, 400};
        mVibrator.vibrate(pattern, 0);

        Intent notificationIntent = new Intent(this, AlarmAlertActivity.class);
        notificationIntent.putExtra("bundle_alarm", bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String alarmTitle = mAlarm.getTitleAlarm();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_ALARM)
                .setContentTitle(getString(R.string.alarm))
                .setContentText(alarmTitle)
                .setSmallIcon(R.drawable.alarm)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true)
                .addAction(R.drawable.alarm_off, getString(R.string.cancel), getPendingIntent(this))
                .build();

        startForeground(2, notification);
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        return START_STICKY;
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("action_alarm", AlarmService.ACTION_CANCEL);
        return PendingIntent.getBroadcast(context.getApplicationContext(), AlarmService.ACTION_CANCEL, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mVibrator.cancel();
        super.onDestroy();
    }
}
