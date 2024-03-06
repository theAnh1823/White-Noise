package com.example.whitenoiseapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whitenoiseapplication.fragment.AlarmFragment;
import com.example.whitenoiseapplication.fragment.SleepCalculatorFragment;
import com.example.whitenoiseapplication.fragment.HomeFragment;
import com.example.whitenoiseapplication.fragment.SettingFragment;

public class ViewPager2MainAdapter extends FragmentStateAdapter {

    public ViewPager2MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new SleepCalculatorFragment();
            case 2:
                return new AlarmFragment();
            case 3:
                return new SettingFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
