package com.example.whitenoiseapplication.model;

import androidx.annotation.Keep;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Keep
public class Audio implements Serializable {
    public static final int TYPE_GRID = 1;
    public static final int TYPE_LIST = 2;
    private int id;
    private int typeDisplay;
    private String imageResource;
    private String audioResource;
    private String nameAudio;
    private boolean isFavorite;
    private boolean isBlocked;

    public Audio() {
    }

    public Audio(int id, String imageResource, String audioResource, String text, boolean isFavorite, boolean isBlocked) {
        this.id = id;
        this.imageResource = imageResource;
        this.audioResource = audioResource;
        this.nameAudio = text;
        this.isFavorite = isFavorite;
        this.isBlocked = isBlocked;
    }

    @PropertyName("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PropertyName("isBlocked")
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @PropertyName("isFavorite")
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getAudioResource() {
        return audioResource;
    }

    public void setAudioResource(String audioResource) {
        this.audioResource = audioResource;
    }

    public int getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(int typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getNameAudio() {
        return nameAudio;
    }

    public void setNameAudio(String nameAudio) {
        this.nameAudio = nameAudio;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "typeDisplay=" + typeDisplay +
                ", imageResource='" + imageResource + '\'' +
                ", audioResource='" + audioResource + '\'' +
                ", nameAudio='" + nameAudio + '\'' +
                ", isFavorite=" + isFavorite +
                ", isBlocked=" + isBlocked +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isBlocked", isBlocked);
        result.put("isFavorite", isFavorite);
        return result;
    }

}
