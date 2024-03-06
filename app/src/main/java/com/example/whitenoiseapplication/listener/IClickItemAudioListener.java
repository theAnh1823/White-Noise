package com.example.whitenoiseapplication.listener;

import com.example.whitenoiseapplication.model.Audio;

public interface IClickItemAudioListener {
    void onClickItemAudio(Audio audio);
    void onClickMore(Audio audio);
    void onClickFavorite(Audio audio);

}
