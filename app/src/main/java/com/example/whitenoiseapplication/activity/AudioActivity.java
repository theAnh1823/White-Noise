package com.example.whitenoiseapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ActivityAudioBinding;
import com.example.whitenoiseapplication.databinding.ActivityMainBinding;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.CountDownManager;
import com.example.whitenoiseapplication.service.AudioService;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AudioActivity extends AppCompatActivity {
    private ActivityAudioBinding binding;
    private CountDownManager countDownManager;
    private Audio mAudio;
    private boolean isPlaying;
    private int actionAudio;
    private long millisecond;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAudio = (Audio) bundle.get("object_audio");
                isPlaying = bundle.getBoolean("status_player");
                actionAudio = bundle.getInt("action_audio");

                handleLayoutAudio(actionAudio);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        //set navigation bar and status bar to transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        getExtrasFromMainActivity();
        handleLayoutAudio(actionAudio);
        countDownManager = CountDownManager.initInstance(ActivityMainBinding.inflate(getLayoutInflater()).tvCountdownTimer, () -> {
            sendActionToService(AudioService.ACTION_CLOSE);
            binding.tvCountdownTimer.setVisibility(View.GONE);
        });
        countDownManager.setTvAudioActivity(binding.tvCountdownTimer);
//        setCountDownTimer();

        Glide.with(this).load(mAudio.getImageResource()).centerCrop().transform(new BlurTransformation(25, 5)).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.layoutActivityAudio.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        binding.btnBackActivityAudio.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.imgTimerCircle.setOnClickListener(v -> openDialogSetTimer());

        binding.btnPlayOrPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseCountDownTimer();
                sendActionToService(AudioService.ACTION_PAUSE);
                binding.rippleBackground.stopRippleAnimation();
            } else {
                resumeCountDownTimer();
                sendActionToService(AudioService.ACTION_RESUME);
                binding.rippleBackground.startRippleAnimation();
            }
        });
    }

    @Override
    protected void onResume() {
        setCountDownTimer();
        super.onResume();
    }

    private void setCountDownTimer() {
        binding.tvCountdownTimer.setText(countDownManager.millisToTimeFormat(countDownManager.getTimeRemaining()));
        if (countDownManager.isTimerRunning() && countDownManager.getTimeRemaining() > 0) {
            countDownManager.startTimer();
        } else if (countDownManager.getTimeRemaining() == 0) {
            binding.tvCountdownTimer.setVisibility(View.GONE);
        }
    }

    private void openDialogSetTimer() {
        final int[] checkedItem = new int[1];
        final long[] millisInFuture = new long[10];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_timer)
                .setSingleChoiceItems(R.array.set_timer, -1, (dialog, which) -> {
                    checkedItem[0] = which;
                    switch (which) {
                        case 0:
                            millisInFuture[0] = 900000;
                            break;
                        case 1:
                            millisInFuture[1] = 1800000;
                            break;
                        case 2:
                            millisInFuture[2] = 3600000;
                            break;
                        case 3:
                            millisInFuture[3] = 7200000;
                            break;
                        case 4:
                            openTimePickerDialog();
                            break;
                        case 5:
                            binding.tvCountdownTimer.setVisibility(View.GONE);
                            countDownManager.resetTimer();
                            break;
                    }
                })
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (checkedItem[0] < 4) {
                        sendRunTimeToService(millisInFuture[checkedItem[0]]);
                        countDownManager.resetTimer();
                        startCountDownTimer(millisInFuture[checkedItem[0]]);
                        sendActionToService(AudioService.ACTION_RESUME);
                        binding.rippleBackground.startRippleAnimation();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void startCountDownTimer(long l) {
        countDownManager.setTimeRemaining(l);
        binding.tvCountdownTimer.setVisibility(View.VISIBLE);
        countDownManager.startTimer();
    }

    private void pauseCountDownTimer() {
        if (countDownManager != null)
            countDownManager.pauseTimer();
    }

    private void resumeCountDownTimer() {
        if (!countDownManager.isTimerRunning() && countDownManager.getTimeRemaining() > 0) {
            if (countDownManager != null)
                countDownManager.resumeTimer();
        }
    }

    private void openTimePickerDialog() {
        int hourDefault = 0;
        int minuteDefault = 20;
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                millisecond = ((hourOfDay * 60L) + minute) * 60000;
                countDownManager.resetTimer();
                startCountDownTimer(millisecond);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, hourDefault, minuteDefault, true);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void getExtrasFromMainActivity() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mAudio = (Audio) bundle.get("object_audio");
            isPlaying = bundle.getBoolean("status_player");
            actionAudio = bundle.getInt("action_audio");
        }
    }

    private void handleLayoutAudio(int actionAudio) {
        switch (actionAudio) {
            case AudioService.ACTION_START:
            case AudioService.ACTION_RESUME:
                binding.btnPlayOrPause.setImageResource(R.drawable.pause_40dp);
                binding.rippleBackground.startRippleAnimation();
                break;
            case AudioService.ACTION_PAUSE:
                binding.btnPlayOrPause.setImageResource(R.drawable.play_40dp);
                binding.rippleBackground.stopRippleAnimation();
                break;
            default:
                getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra("action_audio_service", action);
        this.startService(intent);
    }

    private void sendRunTimeToService(long millisInFuture) {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra("run_time_audio_service", millisInFuture);
        this.startService(intent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}