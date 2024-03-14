package com.example.whitenoiseapplication.model;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CountDownManager {
    @SuppressLint("StaticFieldLeak")
    private static CountDownManager instance;
    private CountDownTimer countDownTimer;
    private final TextView tvMainActivity;
    private TextView tvAudioActivity;
    private final IFinishCountDown finishCountDown;
    private long timeRemaining;
    private boolean isTimerRunning;

    public static CountDownManager initInstance(TextView tvMainActivity, IFinishCountDown finishCountDown) {
        if (instance == null) {
            instance = new CountDownManager(tvMainActivity, finishCountDown);
        }
        return instance;
    }

    public static CountDownManager getInstance() throws Exception {
        if (instance == null) {
            return null;
        } else {
            return instance;
        }
    }

    public CountDownManager(TextView tvMainActivity,  IFinishCountDown finishCountDown) {
        this.tvMainActivity = tvMainActivity;
        this.finishCountDown = finishCountDown;
    }

    public void startTimer() {
        if (!isTimerRunning) {
            tvMainActivity.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(timeRemaining, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeRemaining = millisUntilFinished;
                    if (tvAudioActivity != null)
                        tvAudioActivity.setText(millisToTimeFormat(timeRemaining));
                    tvMainActivity.setText(millisToTimeFormat(timeRemaining));
                }

                @Override
                public void onFinish() {
                    finishCountDown.onFinish();
                }
            }.start();
        }
        isTimerRunning = true;
    }

    @SuppressLint("DefaultLocale")
    public String millisToTimeFormat(long millisUntilFinished) {
        long hour = (millisUntilFinished / 3600000) % 24;
        long min = (millisUntilFinished / 60000) % 60;
        long sec = (millisUntilFinished / 1000) % 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
        return String.format("%02d:%02d", min, sec);
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    public void resumeTimer() {
        if (countDownTimer != null) {
            startTimer();
        }
    }

    public void resetTimer() {
        pauseTimer();
        timeRemaining = 0;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTvAudioActivity(TextView tvAudioActivity) {
        this.tvAudioActivity = tvAudioActivity;
    }

    public interface IFinishCountDown {
        void onFinish();
    }
}
