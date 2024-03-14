package com.example.whitenoiseapplication.model;

import android.util.Log;

public class TimeSingleton {
    private static TimeSingleton timeSingleton;
    private long timeRemaining;
    private boolean isTimerRunning;
    private TimeSingleton() {
    }
    public static TimeSingleton getInstance(){
        if (timeSingleton == null){
            timeSingleton = new TimeSingleton();
        }
        return timeSingleton;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
