
package com.example.whitenoiseapplication.model;
public class Sleep {
    public static final int pickTimeWakeUp = 1;
    public static final int sleepNow = 2;
    private String sleepingOrder;
    private String hourSleep;
    private String infoItem;

    public Sleep(String sleepingOrder, String hourSleep, String infoItem) {
        this.sleepingOrder = sleepingOrder;
        this.hourSleep = hourSleep;
        this.infoItem = infoItem;
    }

    public String getSleepingOrder() {
        return sleepingOrder;
    }

    public void setSleepingOrder(String sleepingOrder) {
        this.sleepingOrder = sleepingOrder;
    }

    public String getHourSleep() {
        return hourSleep;
    }

    public void setHourSleep(String hourSleep) {
        this.hourSleep = hourSleep;
    }

    public String getInfoItem() {
        return infoItem;
    }

    public void setInfoItem(String infoItem) {
        this.infoItem = infoItem;
    }
}


