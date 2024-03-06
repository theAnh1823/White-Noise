package com.example.whitenoiseapplication.model;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.whitenoiseapplication.receiver.AlarmReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey
    @NonNull
    private int alarmId;
    private int alarmHour, alarmMinute;
    private String titleAlarm, repeatModeAlarm, nameRingtoneAlarm;
    private int ringToneAlarm;
    private boolean isAlarmEnabled, recurring, repeatForDaysOfWeek;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    public Alarm() {
    }

    public Alarm(int alarmId, int alarmHour, int alarmMinute, String repeatModeAlarm, String titleAlarm, String nameRingtoneAlarm, int ringToneAlarm, boolean isAlarmEnabled, boolean recurring, boolean repeatForDaysOfWeek, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        this.alarmId = alarmId;
        this.alarmHour = alarmHour;
        this.alarmMinute = alarmMinute;
        this.repeatModeAlarm = repeatModeAlarm;
        this.titleAlarm = titleAlarm;
        this.nameRingtoneAlarm = nameRingtoneAlarm;
        this.ringToneAlarm = ringToneAlarm;
        this.isAlarmEnabled = isAlarmEnabled;
        this.recurring = recurring;
        this.repeatForDaysOfWeek = repeatForDaysOfWeek;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    @SuppressLint("MissingPermission")
    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("object_alarm", this);
        intent.putExtra("bundle_alarm", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (!recurring) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (repeatForDaysOfWeek){
            for (int i : getListDayOfWeek()){
                setWeeklyAlarm(alarmManager, calendar, i, pendingIntent);
            }
        }
        else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        this.isAlarmEnabled = true;
    }

    private void setWeeklyAlarm(AlarmManager alarmManager, Calendar calendar, int dayOfWeek, PendingIntent pendingIntent) {
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_WEEK, 7);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private List<Integer> getListDayOfWeek(){
        List<Integer> list = new ArrayList<>();
        if (isMonday())
            list.add(Calendar.MONDAY);
        if (isTuesday())
            list.add(Calendar.TUESDAY);
        if (isWednesday())
            list.add(Calendar.WEDNESDAY);
        if (isThursday())
            list.add( Calendar.THURSDAY);
        if (isFriday())
            list.add(Calendar.FRIDAY);
        if (isSaturday())
            list.add(Calendar.SATURDAY);
        if (isSunday())
            list.add(Calendar.SUNDAY);
        return list;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.isAlarmEnabled = false;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmHour() {
        return alarmHour;
    }

    public void setAlarmHour(int alarmHour) {
        this.alarmHour = alarmHour;
    }

    public int getAlarmMinute() {
        return alarmMinute;
    }

    public void setAlarmMinute(int alarmMinute) {
        this.alarmMinute = alarmMinute;
    }

    public String getRepeatModeAlarm() {
        return repeatModeAlarm;
    }

    public void setRepeatModeAlarm(String repeatModeAlarm) {
        this.repeatModeAlarm = repeatModeAlarm;
    }

    public String getTitleAlarm() {
        return titleAlarm;
    }

    public void setTitleAlarm(String titleAlarm) {
        this.titleAlarm = titleAlarm;
    }

    public String getNameRingtoneAlarm() {
        return nameRingtoneAlarm;
    }

    public void setNameRingtoneAlarm(String nameRingtoneAlarm) {
        this.nameRingtoneAlarm = nameRingtoneAlarm;
    }

    public int getRingToneAlarm() {
        return ringToneAlarm;
    }

    public void setRingToneAlarm(int ringToneAlarm) {
        this.ringToneAlarm = ringToneAlarm;
    }

    public boolean isAlarmEnabled() {
        return isAlarmEnabled;
    }

    public void setAlarmEnabled(boolean alarmEnabled) {
        isAlarmEnabled = alarmEnabled;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isRepeatForDaysOfWeek() {
        return repeatForDaysOfWeek;
    }

    public void setRepeatForDaysOfWeek(boolean repeatForDaysOfWeek) {
        this.repeatForDaysOfWeek = repeatForDaysOfWeek;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "alarmId=" + alarmId +
                ", alarmHour=" + alarmHour +
                ", alarmMinute=" + alarmMinute +
                ", titleAlarm='" + titleAlarm + '\'' +
                ", repeatModeAlarm='" + repeatModeAlarm + '\'' +
                ", nameRingtoneAlarm='" + nameRingtoneAlarm + '\'' +
                ", ringToneAlarm=" + ringToneAlarm +
                ", isAlarmEnabled=" + isAlarmEnabled +
                ", recurring=" + recurring +
                ", repeatForDaysOfWeek=" + repeatForDaysOfWeek +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                '}';
    }
}
