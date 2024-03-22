package com.example.whitenoiseapplication.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.whitenoiseapplication.databinding.LayoutSheetRepeatAlarmBinding;
import com.example.whitenoiseapplication.model.Alarm;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BottomSheetCustomAlarmRepeat extends BottomSheetDialogFragment {
    private LayoutSheetRepeatAlarmBinding binding;
    private final Alarm alarm;
    private List<Integer> listDayOfWeek;
    private List<Boolean> listSelectionDayOfWeek;
    public interface CustomAlarmRepeatListener{
        void clickCustomAlarmRepeat(List<Boolean> selections);
    }
    private CustomAlarmRepeatListener repeatListener;
    public BottomSheetCustomAlarmRepeat(Alarm alarm) {
        this.alarm = alarm;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = LayoutSheetRepeatAlarmBinding.inflate(LayoutInflater.from(getContext()));
        bottomSheetDialog.setContentView(binding.getRoot());

        listDayOfWeek = new ArrayList<>();
        listSelectionDayOfWeek = new ArrayList<>();

        changeCheckBoxState(alarm);
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSaveRepeatSetting.setOnClickListener(v -> {
            getListDayOfWeek();
            getListSelectionDayOfWeek();
            repeatListener.clickCustomAlarmRepeat(listSelectionDayOfWeek);
        });
        return bottomSheetDialog;
    }

    private void getListDayOfWeek() {
        if (binding.checkBoxMonday.isChecked()){
            listDayOfWeek.add(Calendar.MONDAY);
        }
        if (binding.checkBoxTuesday.isChecked()){
            listDayOfWeek.add(Calendar.TUESDAY);
        }
        if (binding.checkBoxWednesday.isChecked()){
            listDayOfWeek.add(Calendar.WEDNESDAY);
        }
        if (binding.checkBoxThursday.isChecked()){
            listDayOfWeek.add(Calendar.THURSDAY);
        }
        if (binding.checkBoxFriday.isChecked()){
            listDayOfWeek.add(Calendar.FRIDAY);
        }
        if (binding.checkBoxSaturday.isChecked()){
            listDayOfWeek.add(Calendar.SATURDAY);
        }
        if (binding.checkBoxSunday.isChecked()){
            listDayOfWeek.add(Calendar.SUNDAY);
        }
    }

    private void getListSelectionDayOfWeek(){
        listSelectionDayOfWeek.add(binding.checkBoxMonday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxTuesday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxWednesday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxThursday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxFriday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxSaturday.isChecked());
        listSelectionDayOfWeek.add(binding.checkBoxSunday.isChecked());
    }

    private void changeCheckBoxState(Alarm alarm){
        binding.checkBoxMonday.setChecked(alarm.isMonday());
        binding.checkBoxTuesday.setChecked(alarm.isTuesday());
        binding.checkBoxWednesday.setChecked(alarm.isWednesday());
        binding.checkBoxThursday.setChecked(alarm.isThursday());
        binding.checkBoxFriday.setChecked(alarm.isFriday());
        binding.checkBoxSaturday.setChecked(alarm.isSaturday());
        binding.checkBoxSunday.setChecked(alarm.isSunday());
    }

    public void setAlarmRepeatListener(CustomAlarmRepeatListener repeatListener){
        this.repeatListener = repeatListener;
    }
}
