package com.example.whitenoiseapplication.fragment;

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
import com.example.whitenoiseapplication.databinding.SleepCalculatorFragmentBinding;

public class SleepCalculatorFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SleepCalculatorFragmentBinding binding = SleepCalculatorFragmentBinding.inflate(inflater, container, false);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_sleep_calculator, new PickATimeSleepFragment(), null);
        fragmentTransaction.commit();
        return binding.getRoot();
    }

}
