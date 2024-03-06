package com.example.whitenoiseapplication.model;

import android.util.Log;

public class TimeSingleton {
    private static TimeSingleton timeSingleton;
    private long timeRemaining;
    private boolean isTimeRunning;
    private TimeSingleton() {
    }
    public static TimeSingleton getInstance(){
        if (timeSingleton == null){
            timeSingleton = new TimeSingleton();
        }
        return timeSingleton;
    }

    public boolean isTimeRunning() {
        return isTimeRunning;
    }

    public void setTimeRunning(boolean timeRunning) {
        isTimeRunning = timeRunning;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
