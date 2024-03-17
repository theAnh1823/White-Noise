package com.example.whitenoiseapplication.util;

import android.content.Context;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Alarm;

import java.util.ArrayList;
import java.util.List;

public final class DayIdsStringConverter {
    public static String getStringDaysOfWeek(Alarm alarm, Context context) {
        String str = "";
        List<Integer> listDayIds = getListDayOfWeek(alarm);
        if (listDayIds.size() == 7) {
            return context.getString(R.string.daily);
        }
        for (int i = 0; i < listDayIds.size(); i++) {
            if (i == listDayIds.size() - 1)
                str += context.getString(listDayIds.get(i));
            else
                str += context.getString(listDayIds.get(i)) + ", ";
        }
        return str;
    }

    private static List<Integer> getListDayOfWeek(Alarm alarm) {
        List<Integer> list = new ArrayList<>();
        if (alarm.isMonday()){
            list.add(R.string.monday_abbreviation);
        }
        if (alarm.isTuesday()){
            list.add(R.string.tuesday_abbreviation);
        }
        if (alarm.isWednesday()){
            list.add(R.string.wednesday_abbreviation);
        }
        if (alarm.isThursday()){
            list.add(R.string.thursday_abbreviation);
        }
        if (alarm.isFriday()){
            list.add(R.string.friday_abbreviation);
        }
        if (alarm.isSaturday()){
            list.add(R.string.saturday_abbreviation);
        }
        if (alarm.isSunday()){
            list.add(R.string.sunday_abbreviation);
        }
        return list;
    }
}
