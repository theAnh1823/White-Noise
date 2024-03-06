package com.example.whitenoiseapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.whitenoiseapplication.service.AudioService;

public class AudioReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int actionAudio = intent.getIntExtra("action_audio", 0);

        Intent intentService = new Intent(context, AudioService.class);
        intentService.putExtra("action_audio_service", actionAudio);
        context.startService(intentService);
    }
}
