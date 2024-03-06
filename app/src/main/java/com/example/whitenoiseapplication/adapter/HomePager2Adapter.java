package com.example.whitenoiseapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whitenoiseapplication.fragment.AllAudioFragment;
import com.example.whitenoiseapplication.fragment.FavoriteAudioFragment;

public class HomePager2Adapter extends FragmentStateAdapter {

    public HomePager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AllAudioFragment();
            case 1:
                return new FavoriteAudioFragment();
            default:
                return new AllAudioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
