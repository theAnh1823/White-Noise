package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SleepCalculatorAdapter;
import com.example.whitenoiseapplication.model.Sleep;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResultSleepCaculatorFragment extends Fragment {
    private int typeSleepCalculator;
    private ZonedDateTime dateTime;
    private RecyclerView recyclerView;
    private SleepCalculatorAdapter calculatorAdapter;
    private TextView tvBody;
    private Button btnRecalculate;
    private List<Sleep> sleepList;

    public ResultSleepCaculatorFragment(ZonedDateTime dateTime, int typeSleepCalculator) {
        this.dateTime = dateTime;
        this.typeSleepCalculator = typeSleepCalculator;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_result_sleep_calculator, container, false);
        recyclerView = view.findViewById(R.id.rcv_result_sleep_calculator);
        sleepList = getListSleepCalculator();
        calculatorAdapter = new SleepCalculatorAdapter(getContext(), sleepList);
        recyclerView.setAdapter(calculatorAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        
        tvBody = view.findViewById(R.id.tv_body_result_calculate);
        if (typeSleepCalculator == Sleep.pickTimeWakeUp) {
            tvBody.setText(R.string.body_result_sleep_by_time);
        } else if (typeSleepCalculator == Sleep.sleepNow) {
            tvBody.setText(R.string.body_result_sleep_now);
        }
        btnRecalculate = view.findViewById(R.id.btn_recalculate_sleep);
        btnRecalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Sleep> getListSleepCalculator() {
        List<Sleep> list = new ArrayList<>();
        for (int i = 4; i > 0; i--) {
            ZonedDateTime time = dateTime;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (typeSleepCalculator == Sleep.pickTimeWakeUp) {
                    time = time.minusMinutes(15 + 90L * (i+2));
                } else if (typeSleepCalculator == Sleep.sleepNow) {
                    time = time.plusMinutes(15 + 90L * (i+2));
                }
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            String strHourSleep = time.format(dtf);
            String info = ((i+2) * 1.5) + " " + getString(R.string.hours_of_sleep) + ", " + (i+2) + " " + getString(R.string.sleep_cycles);
            list.add(new Sleep(i + "", strHourSleep, info));
        }
        return list;
    }


}
