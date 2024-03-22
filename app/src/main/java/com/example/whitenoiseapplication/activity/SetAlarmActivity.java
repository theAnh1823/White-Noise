package com.example.whitenoiseapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SetAlarmAdapter;
import com.example.whitenoiseapplication.databinding.ActivitySetAlarmBinding;
import com.example.whitenoiseapplication.fragment.BottomSheetAddLabelAlarm;
import com.example.whitenoiseapplication.fragment.BottomSheetCustomAlarmRepeat;
import com.example.whitenoiseapplication.fragment.BottomSheetSetAlarm;
import com.example.whitenoiseapplication.listener.IClickItemBottomSheet;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.model.AlarmSetting;
import com.example.whitenoiseapplication.model.Setting;
import com.example.whitenoiseapplication.util.DateTimeInterval;
import com.example.whitenoiseapplication.util.DayIdsStringConverter;
import com.example.whitenoiseapplication.util.LocaleHelper;
import com.example.whitenoiseapplication.util.TimePickerUtil;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity {
    private ActivitySetAlarmBinding binding;
    private Context context;
    private boolean adjustExistingAlarm;
    private Alarm alarmCreating;
    private Setting settingRepeatAlarm;
    private AlarmsListViewModel alarmsListViewModel;
    private Calendar calendar;
    private SetAlarmAdapter setAlarmAdapter;
    private BottomSheetSetAlarm bottomSheetSetAlarm;
    private BottomSheetAddLabelAlarm bottomSheetAddLabelAlarm;
    private BottomSheetCustomAlarmRepeat sheetCustomAlarmRepeat;
    private List<AlarmSetting> listAlarmSound, listRepeatSelection;
    private List<Boolean> listSelectionDayOfWeek;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        if (isVietnameseLanguage) {
            context = LocaleHelper.setLocale(this, "vi");
        } else {
            context = LocaleHelper.setLocale(this, "en");
        }

        binding.tvTitleSetAlarm.setText(getIntent().getStringExtra("title_alarm_activity"));
        calendar = Calendar.getInstance();
        alarmCreating = new Alarm();
        adjustExistingAlarm = getIntent().getBooleanExtra("adjust_alarm_setting", false);
        if (adjustExistingAlarm) {
            Bundle bundle = getIntent().getBundleExtra("bundle_alarm");
            if (bundle != null) {
                alarmCreating = (Alarm) bundle.getSerializable("object_alarm");
                if (alarmCreating != null)
                    updateUIWithAlarmSetting(alarmCreating);
            }
        } else {
            setDefaultAlarm();
            binding.tvTimeLeftFromNow.setText(getTitleTimeDuration());
        }
        listAlarmSound = getListAlarmSound();
        listRepeatSelection = getListRepeatSelection();
        alarmsListViewModel = new ViewModelProvider(this).get(AlarmsListViewModel.class);

        List<Setting> listSettingAlarm = adjustExistingAlarm ? getListSettingExistAlarm(alarmCreating) : getListSettingDefaultAlarm();
        setAlarmAdapter = new SetAlarmAdapter(this, listSettingAlarm, new IClickItemBottomSheet() {
            @Override
            public void onItemClick(Setting setting) {
                if (setting.getNameItem().equals(getString(R.string.alarm_sound))) {
                    openSheetSetAlarmSound(setting);
                } else if (setting.getNameItem().equals(getString(R.string.repeat_alarm))) {
                    settingRepeatAlarm = setting;
                    openSheetRepeatAlarm(settingRepeatAlarm);
                } else if (setting.getNameItem().equals(getString(R.string.label))) {
                    openSheetAddLabel(setting);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rcvSetAlarm.setLayoutManager(layoutManager);
        binding.rcvSetAlarm.setAdapter(setAlarmAdapter);

        binding.btnClose.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.btnSave.setOnClickListener(v -> {
            alarmCreating.setAlarmEnabled(true);
            if (adjustExistingAlarm) {
                alarmsListViewModel.update(alarmCreating);
            } else {
                alarmsListViewModel.insert(alarmCreating);
            }
            alarmCreating.schedule(getApplicationContext());
            Toast.makeText(this, getTitleTimeDuration(), Toast.LENGTH_LONG).show();
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.timePicker.setIs24HourView(true);
        binding.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarmCreating.setAlarmHour(hourOfDay);
                alarmCreating.setAlarmMinute(minute);
                binding.tvTimeLeftFromNow.setText(getTitleTimeDuration());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void openSheetAddLabel(Setting setting) {
        bottomSheetAddLabelAlarm = new BottomSheetAddLabelAlarm(setting);
        bottomSheetAddLabelAlarm.show(this.getSupportFragmentManager(), bottomSheetAddLabelAlarm.getTag());
        setting.setContentItem(setting.getContentItem());
        setAlarmAdapter.notifyDataSetChanged();
        bottomSheetAddLabelAlarm.setAddLabelListener(new BottomSheetAddLabelAlarm.AddLabelListener() {
            @Override
            public void onAddLabelButtonClicked(String labelText) {
                setting.setContentItem(labelText);
                alarmCreating.setTitleAlarm(labelText);
                setAlarmAdapter.notifyDataSetChanged();
                bottomSheetAddLabelAlarm.dismiss();
            }
        });
    }

    private void openSheetRepeatAlarm(Setting setting) {
        bottomSheetSetAlarm = new BottomSheetSetAlarm(getString(R.string.repeat_alarm), listRepeatSelection, new IClickItemByPosition() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(int position) {
                if (position == listRepeatSelection.size() - 1) {
                    openSheetCustomRepeat(setting);
                } else {
                    if (position == listRepeatSelection.size() - 2) {
                        setWeekdaysAlarm();
                        setWeekendAlarm(false);
                    }
                    if (position > 0) {
                        setWeekdaysAlarm();
                        setWeekendAlarm(true);
                        alarmCreating.setRecurring(true);
                    }
                    setting.setContentItem(getString(listRepeatSelection.get(position).getIdName()));
                    alarmCreating.setRepeatModeId(listRepeatSelection.get(position).getIdName());
                    binding.tvTimeLeftFromNow.setText(getTitleTimeDuration());
                    setAlarmAdapter.notifyDataSetChanged();
                }
                bottomSheetSetAlarm.dismiss();
            }
        });
        bottomSheetSetAlarm.show(this.getSupportFragmentManager(), bottomSheetSetAlarm.getTag());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void openSheetCustomRepeat(Setting setting) {
        sheetCustomAlarmRepeat = new BottomSheetCustomAlarmRepeat(alarmCreating);
        sheetCustomAlarmRepeat.show(this.getSupportFragmentManager(), bottomSheetSetAlarm.getTag());
        sheetCustomAlarmRepeat.setAlarmRepeatListener((selections) -> {
            listSelectionDayOfWeek = selections;
            setRepeatedAlarmDayOfWeek();
            setting.setContentItem(DayIdsStringConverter.getStringDaysOfWeek(alarmCreating, context));
            alarmCreating.setRepeatModeId(R.string.custom);
            alarmCreating.setRecurring(true);
            alarmCreating.setRepeatForDaysOfWeek(true);
            binding.tvTimeLeftFromNow.setText(getTitleTimeDuration());
            setAlarmAdapter.notifyDataSetChanged();
            sheetCustomAlarmRepeat.dismiss();
        });
    }

    private void openSheetSetAlarmSound(Setting setting) {
        bottomSheetSetAlarm = new BottomSheetSetAlarm(getString(R.string.alarm_sound), listAlarmSound, new IClickItemByPosition() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(int position) {
                bottomSheetSetAlarm.setSaveListener(() -> {
                    setting.setContentItem(getString(listAlarmSound.get(position).getIdName()));
                    alarmCreating.setRingtoneId(listAlarmSound.get(position).getIdName());
                    alarmCreating.setRingToneAlarm(listAlarmSound.get(position).getResourceAudio());
                    setAlarmAdapter.notifyDataSetChanged();
                    bottomSheetSetAlarm.dismiss();
                });
            }
        });
        bottomSheetSetAlarm.show(this.getSupportFragmentManager(), bottomSheetSetAlarm.getTag());
    }

    private List<Setting> getListSettingDefaultAlarm() {
        List<Setting> list = new ArrayList<>();
        list.add(new Setting(getString(R.string.alarm_sound), getString(R.string.default_setting)));
        list.add(new Setting(getString(R.string.repeat_alarm), getString(R.string.once)));
        list.add(new Setting(getString(R.string.label), ""));
        return list;
    }

    private List<Setting> getListSettingExistAlarm(Alarm alarmCreating) {
        List<Setting> list = new ArrayList<>();
        list.add(new Setting(getString(R.string.alarm_sound), getString(alarmCreating.getRingtoneId())));
        list.add(new Setting(getString(R.string.repeat_alarm), getString(alarmCreating.getRepeatModeId())));
        list.add(new Setting(getString(R.string.label), alarmCreating.getTitleAlarm()));
        return list;
    }

    private List<AlarmSetting> getListAlarmSound() {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting(R.string.default_setting, R.raw.default_ringtone, false));
        list.add(new AlarmSetting(R.string.rooster_crow, R.raw.morning_rooster, false));
        list.add(new AlarmSetting(R.string.piano, R.raw.piano_ringtone, false));
        list.add(new AlarmSetting(R.string.beep_ringtone, R.raw.beep_ringtone, false));
        list.add(new AlarmSetting(R.string.bell_ringtone, R.raw.bell_ringtone, false));

        setItemToTrue(list, getString(alarmCreating.getRingtoneId()));
        return list;
    }

    private List<AlarmSetting> getListRepeatSelection() {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting(R.string.once, false));
        list.add(new AlarmSetting(R.string.daily, false));
        list.add(new AlarmSetting(R.string.mon_to_fri, false));
        list.add(new AlarmSetting(R.string.custom, false));

        setItemToTrue(list, getString(alarmCreating.getRepeatModeId()));
        return list;
    }

    private void setItemToTrue(List<AlarmSetting> list, String str) {
        if (adjustExistingAlarm) {
            for (int i = 0; i < list.size(); i++) {
                if (getString(list.get(i).getIdName()).equals(str)) {
                    list.get(i).setSelected(true);
                }
            }
        } else {
            list.get(0).setSelected(true);
        }
    }

    private void updateUIWithAlarmSetting(Alarm alarm) {
        TimePickerUtil.setHourTimePicker(binding.timePicker, alarm.getAlarmHour());
        TimePickerUtil.setMinuteTimePicker(binding.timePicker, alarm.getAlarmMinute());
        binding.tvTimeLeftFromNow.setText(getTitleTimeDuration());
    }

    private void setDefaultAlarm() {
        alarmCreating.setTitleAlarm("");
        alarmCreating.setAlarmHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarmCreating.setAlarmMinute(calendar.get(Calendar.MINUTE));
        alarmCreating.setRingToneAlarm(R.raw.default_ringtone);
        alarmCreating.setRingtoneId(R.string.default_setting);
        alarmCreating.setRepeatModeId(R.string.once);
        alarmCreating.setRecurring(false);
        alarmCreating.setRepeatForDaysOfWeek(false);
    }

    private void setRepeatedAlarmDayOfWeek() {
        alarmCreating.setMonday(listSelectionDayOfWeek.get(0));
        alarmCreating.setTuesday(listSelectionDayOfWeek.get(1));
        alarmCreating.setWednesday(listSelectionDayOfWeek.get(2));
        alarmCreating.setThursday(listSelectionDayOfWeek.get(3));
        alarmCreating.setFriday(listSelectionDayOfWeek.get(4));
        alarmCreating.setSaturday(listSelectionDayOfWeek.get(5));
        alarmCreating.setSunday(listSelectionDayOfWeek.get(6));
    }

    private void setWeekdaysAlarm() {
        alarmCreating.setMonday(true);
        alarmCreating.setTuesday(true);
        alarmCreating.setWednesday(true);
        alarmCreating.setThursday(true);
        alarmCreating.setFriday(true);
    }

    private void setWeekendAlarm(boolean isEnabled) {
        alarmCreating.setSaturday(isEnabled);
        alarmCreating.setSunday(isEnabled);
    }

    private String getTitleTimeDuration() {
        return DateTimeInterval.getDateTimeInterval(alarmCreating, context);
    }
}