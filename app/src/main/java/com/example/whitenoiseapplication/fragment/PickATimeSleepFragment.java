package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.LayoutSleepCalculatorBinding;
import com.example.whitenoiseapplication.model.Sleep;
import com.example.whitenoiseapplication.util.TimePickerUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PickATimeSleepFragment extends Fragment implements View.OnClickListener{
    private LayoutSleepCalculatorBinding binding;
    private int typeSleepCalculator;
    private ZonedDateTime dateTime;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutSleepCalculatorBinding.inflate(inflater, container, false);
        fragmentManager = getParentFragmentManager();
        binding.timePickerSleepCalculator.setIs24HourView(true);
        binding.btnCalculateSleep.setOnClickListener(this);
        binding.btnSleepNow.setOnClickListener(this);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_calculate_sleep:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    typeSleepCalculator = Sleep.pickTimeWakeUp;
                    dateTime = ZonedDateTime.of(1,1,1,
                            TimePickerUtil.getHourTimePicker(binding.timePickerSleepCalculator),
                            TimePickerUtil.getMinuteTimePicker(binding.timePickerSleepCalculator),
                            0,0, ZoneId.systemDefault());
                }
                break;
            case R.id.btn_sleep_now:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    typeSleepCalculator = Sleep.sleepNow;
                    dateTime = ZonedDateTime.now();
                }
                break;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_sleep_calculator, new ResultSleepCaculatorFragment(dateTime, typeSleepCalculator));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
