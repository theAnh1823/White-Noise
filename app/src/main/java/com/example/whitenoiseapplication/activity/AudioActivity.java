package com.example.whitenoiseapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.TimeSingleton;
import com.example.whitenoiseapplication.service.AudioService;
import com.skyfishjy.library.RippleBackground;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AudioActivity extends AppCompatActivity {
    private ConstraintLayout layout;
    private ImageView btnCollapse, setTimer;
    private TextView tvTitleAudio;
    private TextView tvCountDown;
    private CountDownTimer countDownTimer;
    private TimeSingleton timeSingleton;
    private RippleBackground rippleBackground;
    private ImageButton btnPlayOrPause;
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
        setContentView(R.layout.activity_audio);
        getSupportActionBar().hide();
        //set navigation bar and status bar to transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        timeSingleton = TimeSingleton.getInstance();
        getExtrasFromMainActivity();
        initView();
        setCountDownTimer();

        Glide.with(this).load(mAudio.getImageResource()).centerCrop().transform(new BlurTransformation(25, 5)).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                layout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        btnCollapse.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        setTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSetTimer();
            }
        });

        btnPlayOrPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseCountDownTimer();
                sendActionToService(AudioService.ACTION_PAUSE);
                rippleBackground.stopRippleAnimation();
            } else {
                resumeCountDownTimer();
                sendActionToService(AudioService.ACTION_RESUME);
                rippleBackground.startRippleAnimation();
            }
        });
    }

    private void setCountDownTimer() {
        if (timeSingleton.isTimeRunning() && timeSingleton.getTimeRemaining() > 0)
            startCountDownTimer(timeSingleton.getTimeRemaining());
        else if (timeSingleton.getTimeRemaining() == 0) {
            tvCountDown.setVisibility(View.GONE);
        } else {
            tvCountDown.setText(millisToTimeFormat(timeSingleton.getTimeRemaining()));
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
                    }
                })
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (checkedItem[0] < 4) {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        sendRunTimeToService(millisInFuture[checkedItem[0]]);
                        startCountDownTimer(millisInFuture[checkedItem[0]]);
                        sendActionToService(AudioService.ACTION_RESUME);
                        rippleBackground.startRippleAnimation();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void startCountDownTimer(long l) {
        tvCountDown.setVisibility(View.VISIBLE);
        timeSingleton.setTimeRunning(true);
        timeSingleton.setTimeRemaining(l);
        countDownTimer = new CountDownTimer(l, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeSingleton.setTimeRemaining(millisUntilFinished);
                tvCountDown.setText(millisToTimeFormat(millisUntilFinished));
                Log.e("A", "AUDIO ACTIVITY: " + millisToTimeFormat(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                sendActionToService(AudioService.ACTION_CLOSE);
                tvCountDown.setVisibility(View.GONE);
            }
        }.start();
    }

    @SuppressLint("DefaultLocale")
    private String millisToTimeFormat(long millisUntilFinished) {
        long hour = (millisUntilFinished / 3600000) % 24;
        long min = (millisUntilFinished / 60000) % 60;
        long sec = (millisUntilFinished / 1000) % 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
        return String.format("%02d:%02d", min, sec);
    }

    private void pauseCountDownTimer() {
        timeSingleton.setTimeRunning(false);
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    private void resumeCountDownTimer() {
        timeSingleton.setTimeRunning(true);
        if (timeSingleton.isTimeRunning() && timeSingleton.getTimeRemaining() > 0)
            startCountDownTimer(timeSingleton.getTimeRemaining());
    }

    private void openTimePickerDialog() {
        int hourDefault = 0;
        int minuteDefault = 20;
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                millisecond = ((hourOfDay * 60L) + minute) * 60000;

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

    private void initView() {
        layout = findViewById(R.id.layout_activity_audio);
        btnCollapse = findViewById(R.id.btn_back_activity_audio);
        setTimer = findViewById(R.id.img_activity_audio);
        tvTitleAudio = findViewById(R.id.tv_name_activity_audio);
        tvTitleAudio.setText(mAudio.getNameAudio());
        tvCountDown = findViewById(R.id.tv_countdown_timer);
        rippleBackground = findViewById(R.id.ripple_background);
        btnPlayOrPause = findViewById(R.id.btn_play_or_pause);
        handleLayoutAudio(actionAudio);
    }

    private void handleLayoutAudio(int actionAudio) {
        switch (actionAudio) {
            case AudioService.ACTION_START:
            case AudioService.ACTION_RESUME:
                btnPlayOrPause.setImageResource(R.drawable.pause_40dp);
                rippleBackground.startRippleAnimation();
                break;
            case AudioService.ACTION_PAUSE:
                btnPlayOrPause.setImageResource(R.drawable.play_40dp);
                rippleBackground.stopRippleAnimation();
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
    protected void onStop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}