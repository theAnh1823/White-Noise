package com.example.whitenoiseapplication.service;

import static com.example.whitenoiseapplication.application.MainApplication.CHANNEL_ID_AUDIO;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.whitenoiseapplication.receiver.AudioReceiver;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.activity.MainActivity;
import com.example.whitenoiseapplication.model.Audio;

import java.io.IOException;

public class AudioService extends Service {
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLOSE = 3;
    public static final int ACTION_START = 4;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private long runTimes = 0;
    private Audio mAudio;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("White Noise", "My Service onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Audio audio = (Audio) bundle.get("object_audio");
            if (audio != null) {
                mAudio = audio;
                startAudio(audio);
                sendNotification(audio);
            }
        }

        int actionAudio = intent.getIntExtra("action_audio_service", 0);
        if (runTimes == 0){
            runTimes = intent.getLongExtra("run_time_audio_service", 0);
        }
        handleActionAudio(actionAudio);

        return START_NOT_STICKY;
    }

    private void startAudio(Audio audio) {
        isPlaying = true;
//        if (mediaPlayer == null){
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound_test);
//        }
//        mediaPlayer.start();
//        mediaPlayer.setLooping(true);
//        sendActionToActivity(ACTION_START);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(audio.getAudioResource());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        sendActionToActivity(ACTION_START);
    }

    private void handleActionAudio(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseAudio();
                break;
            case ACTION_RESUME:
                resumeAudio();
                break;
            case ACTION_CLOSE:
                stopSelf();
                sendActionToActivity(ACTION_CLOSE);
                break;
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendActionToActivity(ACTION_PAUSE);
            sendNotification(mAudio);
        }
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendActionToActivity(ACTION_RESUME);
            sendNotification(mAudio);
        }
    }

    private void sendNotification(Audio audio) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Dùng Glide tạo bitmap để setLargeIcon(bitmap) cho Notification
        Glide.with(getApplicationContext()).asBitmap().load(audio.getImageResource()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                showNotification(audio, resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    @SuppressLint("ForegroundServiceType")
    private void showNotification(Audio audio, Bitmap bitmap) {
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "TAG");
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, runTimes)
                .build();
        mediaSessionCompat.setMetadata(metadata);

        PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder().setState(
                        PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE)
                .build();
        mediaSessionCompat.setPlaybackState(stateCompat);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_AUDIO)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.bedtime)
                .setLargeIcon(bitmap)
                .setColor(Color.GREEN)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setOngoing(true)
                .setContentTitle(audio.getNameAudio())
                .setSound(null);

        if (isPlaying) {
            notificationBuilder
                    .addAction(R.drawable.pause_24dp, "Pause", getPendingIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.close_24dp, "Close", getPendingIntent(this, ACTION_CLOSE));
        } else {
            notificationBuilder
                    .addAction(R.drawable.play_24dp, "Play", getPendingIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.close_24dp, "Close", getPendingIntent(this, ACTION_CLOSE));
        }
        Notification customNotification = notificationBuilder.build();

        startForeground(1, customNotification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, AudioReceiver.class);
        intent.putExtra("action_audio", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendActionToActivity(int action) {
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_audio", mAudio);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_audio", action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        Log.e("White Noise", "My Service onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
