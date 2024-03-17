package com.example.whitenoiseapplication.model;

public class AlarmSetting {
    int idName;
    int resourceAudio;
    boolean isSelected;

    public AlarmSetting(int idName, boolean isSelected) {
        this.idName = idName;
        this.isSelected = isSelected;
    }

    public AlarmSetting(int idName, int resourceAudio, boolean isSelected) {
        this.idName = idName;
        this.resourceAudio = resourceAudio;
        this.isSelected = isSelected;
    }

    public int getIdName() {
        return idName;
    }

    public void setIdName(int idName) {
        this.idName = idName;
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
