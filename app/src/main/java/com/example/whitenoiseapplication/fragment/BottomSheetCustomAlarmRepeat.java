package com.example.whitenoiseapplication.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.example.whitenoiseapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BottomSheetCustomAlarmRepeat extends BottomSheetDialogFragment {
    private String strListDayOfWeek = "";
    private List<String> listNameDayOfWeek;
    private List<Integer> listDayOfWeek;
    private List<Boolean> listSelectionDayOfWeek;
    private AppCompatCheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday,
            checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    private AppCompatButton btnSave, btnCancel;
    public interface CustomAlarmRepeatListener{
        void clickCustomAlarmRepeat(String strListDayOfWeek, List<Integer> list, List<Boolean> selections);
    }
    private CustomAlarmRepeatListener repeatListener;
    public BottomSheetCustomAlarmRepeat() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_repeat_alarm, null);
        bottomSheetDialog.setContentView(view);

        listDayOfWeek = new ArrayList<>();
        listNameDayOfWeek = new ArrayList<>();
        listSelectionDayOfWeek = new ArrayList<>();
        initComponents(view);
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
        btnSave.setOnClickListener(v -> {
            getListDayOfWeek();
            getListSelectionDayOfWeek();
            strListDayOfWeek = getStrListDayOfWeek();
            repeatListener.clickCustomAlarmRepeat(strListDayOfWeek, listDayOfWeek, listSelectionDayOfWeek);
        });
        return bottomSheetDialog;
    }

    private void getListDayOfWeek() {
        if (checkBoxMonday.isChecked()){
            listDayOfWeek.add(Calendar.MONDAY);
            listNameDayOfWeek.add(getString(R.string.monday_abbreviation));
        }
        if (checkBoxTuesday.isChecked()){
            listDayOfWeek.add(Calendar.TUESDAY);
            listNameDayOfWeek.add(getString(R.string.tuesday_abbreviation));
        }
        if (checkBoxWednesday.isChecked()){
            listDayOfWeek.add(Calendar.WEDNESDAY);
            listNameDayOfWeek.add(getString(R.string.wednesday_abbreviation));
        }
        if (checkBoxThursday.isChecked()){
            listDayOfWeek.add(Calendar.THURSDAY);
            listNameDayOfWeek.add(getString(R.string.thursday_abbreviation));
        }
        if (checkBoxFriday.isChecked()){
            listDayOfWeek.add(Calendar.FRIDAY);
            listNameDayOfWeek.add(getString(R.string.friday_abbreviation));
        }
        if (checkBoxSaturday.isChecked()){
            listDayOfWeek.add(Calendar.SATURDAY);
            listNameDayOfWeek.add(getString(R.string.saturday_abbreviation));
        }
        if (checkBoxSunday.isChecked()){
            listDayOfWeek.add(Calendar.SUNDAY);
            listNameDayOfWeek.add(getString(R.string.sunday_abbreviation));
        }
    }

    private void getListSelectionDayOfWeek(){
        listSelectionDayOfWeek.add(checkBoxMonday.isChecked());
        listSelectionDayOfWeek.add(checkBoxTuesday.isChecked());
        listSelectionDayOfWeek.add(checkBoxWednesday.isChecked());
        listSelectionDayOfWeek.add(checkBoxThursday.isChecked());
        listSelectionDayOfWeek.add(checkBoxFriday.isChecked());
        listSelectionDayOfWeek.add(checkBoxSaturday.isChecked());
        listSelectionDayOfWeek.add(checkBoxSunday.isChecked());
    }

    private String getStrListDayOfWeek() {
        String str = "";
        if (listNameDayOfWeek.size() == 7){
            return getString(R.string.daily);
        }
        for (int i = 0; i < listNameDayOfWeek.size(); i++) {
            if (i == listNameDayOfWeek.size() - 1)
                str += listNameDayOfWeek.get(i);
            else
                str += listNameDayOfWeek.get(i) + ", ";
        }
        return str;
    }

    public void setAlarmRepeatListener(CustomAlarmRepeatListener repeatListener){
        this.repeatListener = repeatListener;
    };

    private void initComponents(View view) {
        checkBoxMonday = view.findViewById(R.id.checkbox_monday);
        checkBoxTuesday = view.findViewById(R.id.checkbox_tuesday);
        checkBoxWednesday = view.findViewById(R.id.checkbox_wednesday);
        checkBoxThursday = view.findViewById(R.id.checkbox_thursday);
        checkBoxFriday = view.findViewById(R.id.checkbox_friday);
        checkBoxSaturday = view.findViewById(R.id.checkbox_saturday);
        checkBoxSunday = view.findViewById(R.id.checkbox_sunday);
        btnSave = view.findViewById(R.id.btn_save_repeat_setting);
        btnCancel = view.findViewById(R.id.btn_bottom_sheet_cancel);
    }
}
