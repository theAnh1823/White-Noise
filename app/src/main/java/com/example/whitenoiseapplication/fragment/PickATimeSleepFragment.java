package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Sleep;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PickATimeSleepFragment extends Fragment implements View.OnClickListener{
    private int typeSleepCalculator;
    private ZonedDateTime dateTime;
    private FragmentManager fragmentManager;
    private TimePicker timePicker;
    private Button btnSleepCalculate;
    private Button btnSleepNow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_sleep_calculator, container, false);
        fragmentManager = getParentFragmentManager();
        timePicker = view.findViewById(R.id.time_picker_sleep_calculator);
        timePicker.setIs24HourView(true);
        btnSleepCalculate = view.findViewById(R.id.btn_calculate_sleep);
        btnSleepNow = view.findViewById(R.id.btn_sleep_now);
        btnSleepCalculate.setOnClickListener(this);
        btnSleepNow.setOnClickListener(this);
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_calculate_sleep:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    typeSleepCalculator = Sleep.pickTimeWakeUp;
                    dateTime = ZonedDateTime.of(1,1,1,timePicker.getHour(),timePicker.getMinute(),0,0, ZoneId.systemDefault());
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
