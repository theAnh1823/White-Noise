package com.example.whitenoiseapplication.util;

import android.os.Build;
import android.widget.TimePicker;

public final class TimePickerUtil {
    public static int getHourTimePicker(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getHour();
        }
        return timePicker.getCurrentHour();
    }

    public static int getMinuteTimePicker(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        }
        return timePicker.getCurrentMinute();
    }

    public static void setHourTimePicker(TimePicker timePicker, int currentHour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(currentHour);
        } else {
            timePicker.setCurrentHour(currentHour);
        }
    }

    public static void setMinuteTimePicker(TimePicker timePicker, int currentMinute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(currentMinute);
        } else {
            timePicker.setCurrentMinute(currentMinute);
        }
    }
}
