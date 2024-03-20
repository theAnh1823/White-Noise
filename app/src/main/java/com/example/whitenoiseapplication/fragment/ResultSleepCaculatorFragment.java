package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SleepCalculatorAdapter;
import com.example.whitenoiseapplication.databinding.LayoutResultSleepCalculatorBinding;
import com.example.whitenoiseapplication.model.Sleep;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResultSleepCaculatorFragment extends Fragment {
    private final int typeSleepCalculator;
    private final ZonedDateTime dateTime;

    public ResultSleepCaculatorFragment(ZonedDateTime dateTime, int typeSleepCalculator) {
        this.dateTime = dateTime;
        this.typeSleepCalculator = typeSleepCalculator;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutResultSleepCalculatorBinding binding = LayoutResultSleepCalculatorBinding.inflate(inflater, container, false);
        List<Sleep> sleepList = getListSleepCalculator();
        SleepCalculatorAdapter calculatorAdapter = new SleepCalculatorAdapter(getContext(), sleepList);
        binding.rcvResult.setAdapter(calculatorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rcvResult.setLayoutManager(layoutManager);

        if (typeSleepCalculator == Sleep.pickTimeWakeUp) {
            binding.tvBodyResultCalculate.setText(R.string.body_result_sleep_by_time);
        } else if (typeSleepCalculator == Sleep.sleepNow) {
            binding.tvBodyResultCalculate.setText(R.string.body_result_sleep_now);
        }
        binding.btnRecalculateSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Sleep> getListSleepCalculator() {
        List<Sleep> list = new ArrayList<>();
        for (int i = 4; i > 0; i--) {
            ZonedDateTime time = dateTime;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (typeSleepCalculator == Sleep.pickTimeWakeUp) {
                    time = time.minusMinutes(15 + 90L * (i + 2));
                } else if (typeSleepCalculator == Sleep.sleepNow) {
                    time = time.plusMinutes(15 + 90L * (i + 2));
                }
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            String strHourSleep = time.format(dtf);
            String info = ((i + 2) * 1.5) + " " + getString(R.string.hours_of_sleep) + ", " + (i + 2) + " " + getString(R.string.sleep_cycles);
            list.add(new Sleep(i + "", strHourSleep, info));
        }
        return list;
    }


}
