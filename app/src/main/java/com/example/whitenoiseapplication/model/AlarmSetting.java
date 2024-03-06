package com.example.whitenoiseapplication.model;

public class AlarmSetting {
    String nameItem;
    int resourceAudio;
    boolean isSelected;

    public AlarmSetting(String nameItem, boolean isSelected) {
        this.nameItem = nameItem;
        this.isSelected = isSelected;
    }

    public AlarmSetting(String nameItem, int resourceAudio, boolean isSelected) {
        this.nameItem = nameItem;
        this.resourceAudio = resourceAudio;
        this.isSelected = isSelected;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    public int getResourceAudio() {
        return resourceAudio;
    }

    public void setResourceAudio(int resourceAudio) {
        this.resourceAudio = resourceAudio;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
