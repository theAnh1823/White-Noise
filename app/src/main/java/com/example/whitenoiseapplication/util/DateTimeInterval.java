package com.example.whitenoiseapplication.util;

import android.content.Context;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class DateTimeInterval {
    private static String StrDateTimeDuration;

    public static String getDateTimeInterval(Alarm alarm, Context context) {
        Calendar calendar = Calendar.getInstance();
        StrDateTimeDuration = context.getString(R.string.alarm_in);
        String settingRepeatAlarm = alarm.getRepeatModeAlarm();
        if (settingRepeatAlarm.equals(context.getString(R.string.mon_to_fri))) {
            if (calendar.get(Calendar.DAY_OF_WEEK) >= 5)
                setDateDuration(Calendar.MONDAY, context);
        } else if (!settingRepeatAlarm.equals(context.getString(R.string.once)) && !settingRepeatAlarm.equals(context.getString(R.string.daily))) {
            setDateDuration(findNearestDay(alarm), context);
        }
        setTimeDuration(alarm.getAlarmHour(), alarm.getAlarmMinute(), context);
        return StrDateTimeDuration;
    }

    private static void setDateDuration(int specifiedDayOfWeek, Context context) {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayAsPart = (specifiedDayOfWeek - currentDayOfWeek + 7) % 7;
        String strDay = dayAsPart > 1 ? context.getString(R.string.days) : context.getString(R.string.day);
        if (dayAsPart > 0) {
            StrDateTimeDuration += " " + dayAsPart + " " + strDay;
        }
    }

    private static void setTimeDuration(int hourOfDay, int minute, Context context) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int hourDiff = hourOfDay - currentHour;
        int minuteDiff = minute - currentMinute;

        if (minuteDiff < 0) {
            hourDiff--;
            minuteDiff += 60;
        }
        if (hourDiff < 0) {
            hourDiff += 24;
        } else if (hourDiff == 0 && minuteDiff == 0)
            hourDiff = 24;
        String strHour = hourDiff > 1 ? context.getString(R.string.hours) : context.getString(R.string.hour);
        String strMinute = minuteDiff > 1 ? context.getString(R.string.minutes) : context.getString(R.string.minute);
        StrDateTimeDuration += " " + hourDiff + " " + strHour + " " + minuteDiff + " " + strMinute;
    }

    private static int findNearestDay(Alarm alarm) {
        List<Integer> listDayOfWeek = new ArrayList<>();
        getListDayOfWeek(alarm, listDayOfWeek);
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < listDayOfWeek.size(); i++) {
            if (calendar.get(Calendar.DAY_OF_WEEK) - listDayOfWeek.get(i) <= 0) {
                return listDayOfWeek.get(i);
            }
        }
        return listDayOfWeek.get(0);
    }

    private static void getListDayOfWeek(Alarm alarm, List<Integer> listDayOfWeek) {
        if (alarm.isMonday()) {
            listDayOfWeek.add(Calendar.MONDAY);
        }
        if (alarm.isTuesday()) {
            listDayOfWeek.add(Calendar.TUESDAY);
        }
        if (alarm.isWednesday()) {
            listDayOfWeek.add(Calendar.WEDNESDAY);
        }
        if (alarm.isThursday()) {
            listDayOfWeek.add(Calendar.THURSDAY);
        }
        if (alarm.isFriday()) {
            listDayOfWeek.add(Calendar.FRIDAY);
        }
        if (alarm.isSaturday()) {
            listDayOfWeek.add(Calendar.SATURDAY);
        }
        if (alarm.isSunday()) {
            listDayOfWeek.add(Calendar.SUNDAY);
        }
    }
}
