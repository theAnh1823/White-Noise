package com.example.whitenoiseapplication.model;

import java.util.ArrayList;
import java.util.List;

public class Setting {
    public static int layoutItemSetting = 1;
    public static int layoutItemLanguage = 2;
    private int imageResource;
    private String nameItem;
    private String contentItem;
    private int viewType;
    private boolean isSelected;

    public Setting(String nameItem, String contentItem) {
        this.nameItem = nameItem;
        this.contentItem = contentItem;
    }

    public Setting(int viewType, int imageResource, String nameItem) {
        this.viewType = viewType;
        this.imageResource = imageResource;
        this.nameItem = nameItem;
    }

    public Setting(int viewType, int imageResource, String nameItem, String contentItem) {
        this.viewType = viewType;
        this.imageResource = imageResource;
        this.nameItem = nameItem;
        this.contentItem = contentItem;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getContentItem() {
        return contentItem;
    }

    public void setContentItem(String contentItem) {
        this.contentItem = contentItem;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getNameItem() {
        return nameItem;
    }

    public void setNameItem(String nameItem) {
        this.nameItem = nameItem;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "imageResource=" + imageResource +
                ", nameItem='" + nameItem + '\'' +
                ", contentItem='" + contentItem + '\'' +
                ", viewType=" + viewType +
                ", isSelected=" + isSelected +
                '}';
    }
}
