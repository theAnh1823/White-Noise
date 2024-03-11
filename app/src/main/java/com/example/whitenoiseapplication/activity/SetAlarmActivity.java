package com.example.whitenoiseapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SetAlarmAdapter;
import com.example.whitenoiseapplication.fragment.BottomSheetAddLabelAlarm;
import com.example.whitenoiseapplication.fragment.BottomSheetCustomAlarmRepeat;
import com.example.whitenoiseapplication.fragment.BottomSheetSetAlarm;
import com.example.whitenoiseapplication.listener.IClickItemBottemSheet;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.model.AlarmSetting;
import com.example.whitenoiseapplication.model.Setting;
import com.example.whitenoiseapplication.util.DateTimeInterval;
import com.example.whitenoiseapplication.util.LocaleHelper;
import com.example.whitenoiseapplication.util.TimePickerUtil;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;
import com.example.whitenoiseapplication.viewmodel.CreateAlarmViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class SetAlarmActivity extends AppCompatActivity {
    private Context context;
    private boolean adjustExistingAlarm;
    private Alarm alarmCreating;
    private Setting settingRepeatAlarm;
    private CreateAlarmViewModel createAlarmViewModel;
    private AlarmsListViewModel alarmsListViewModel;
    private Calendar calendar;
    private ImageView btnClose, btnSaveAlarm;
    private AppCompatTextView tvHeader, tvTitle;
    private TimePicker timePicker;
    private RecyclerView recyclerView;
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
        setContentView(R.layout.activity_set_alarm);
        getSupportActionBar().hide();
        initComponents();

        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        if (isVietnameseLanguage) {
            context = LocaleHelper.setLocale(this, "vi");
        } else {
            context = LocaleHelper.setLocale(this, "en");
        }

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
            tvTitle.setText(getTitleTimeDuration());
        }
        listAlarmSound = getListAlarmSound();
        listRepeatSelection = getListRepeatSelection();
        createAlarmViewModel = new ViewModelProvider(this).get(CreateAlarmViewModel.class);
        alarmsListViewModel = new ViewModelProvider(this).get(AlarmsListViewModel.class);

        List<Setting> listSettingAlarm = adjustExistingAlarm ? getListSettingExistAlarm(alarmCreating) : getListSettingDefaultAlarm();
        setAlarmAdapter = new SetAlarmAdapter(this, listSettingAlarm, new IClickItemBottemSheet() {
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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(setAlarmAdapter);

        btnClose.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnSaveAlarm.setOnClickListener(v -> {
            alarmCreating.setAlarmEnabled(true);
            if (adjustExistingAlarm) {
                alarmsListViewModel.update(alarmCreating);
            } else {
                createAlarmViewModel.insert(alarmCreating);
            }
            alarmCreating.schedule(getApplicationContext());
            Toast.makeText(this, getTitleTimeDuration(), Toast.LENGTH_LONG).show();
            getOnBackPressedDispatcher().onBackPressed();
        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarmCreating.setAlarmHour(hourOfDay);
                alarmCreating.setAlarmMinute(minute);
                tvTitle.setText(getTitleTimeDuration());
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
                        setWeekdayAlarm();
                        alarmCreating.setRepeatForDaysOfWeek(true);
                    }
                    boolean recurring = position > 0;
                    alarmCreating.setRecurring(recurring);
                    setting.setContentItem(listRepeatSelection.get(position).getNameItem());
                    alarmCreating.setRepeatModeAlarm(listRepeatSelection.get(position).getNameItem());
                    tvTitle.setText(getTitleTimeDuration());
                    setAlarmAdapter.notifyDataSetChanged();
                }
                bottomSheetSetAlarm.dismiss();
            }
        });
        bottomSheetSetAlarm.show(this.getSupportFragmentManager(), bottomSheetSetAlarm.getTag());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void openSheetCustomRepeat(Setting setting) {
        sheetCustomAlarmRepeat = new BottomSheetCustomAlarmRepeat();
        sheetCustomAlarmRepeat.show(this.getSupportFragmentManager(), bottomSheetSetAlarm.getTag());
        sheetCustomAlarmRepeat.setAlarmRepeatListener((strListDayOfWeek, list, selections) -> {
            listSelectionDayOfWeek = selections;
            setRepeatedAlarmDayOfWeek();
            setting.setContentItem(strListDayOfWeek);
            alarmCreating.setRepeatModeAlarm(strListDayOfWeek);
            alarmCreating.setRecurring(true);
            alarmCreating.setRepeatForDaysOfWeek(true);
            tvTitle.setText(getTitleTimeDuration());
            setAlarmAdapter.notifyDataSetChanged();
            sheetCustomAlarmRepeat.dismiss();
        });
    }

    private void openSheetSetAlarmSound(Setting setting) {
        bottomSheetSetAlarm = new BottomSheetSetAlarm(getString(R.string.alarm_sound), listAlarmSound, new IClickItemByPosition() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(int position) {
                bottomSheetSetAlarm.setSaveListener(new BottomSheetSetAlarm.SaveListener() {
                    @Override
                    public void saveAlarmSetting() {
                        setting.setContentItem(listAlarmSound.get(position).getNameItem());
                        alarmCreating.setNameRingtoneAlarm(listAlarmSound.get(position).getNameItem());
                        alarmCreating.setRingToneAlarm(listAlarmSound.get(position).getResourceAudio());
                        setAlarmAdapter.notifyDataSetChanged();
                        bottomSheetSetAlarm.dismiss();
                    }
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
        list.add(new Setting(getString(R.string.alarm_sound), alarmCreating.getNameRingtoneAlarm()));
        list.add(new Setting(getString(R.string.repeat_alarm), alarmCreating.getRepeatModeAlarm()));
        list.add(new Setting(getString(R.string.label), alarmCreating.getTitleAlarm()));
        return list;
    }

    private List<AlarmSetting> getListAlarmSound() {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting(getString(R.string.default_setting), R.raw.default_ringtone, false));
        list.add(new AlarmSetting(getString(R.string.rooster_crow), R.raw.morning_rooster, false));
        list.add(new AlarmSetting(getString(R.string.piano), R.raw.piano_ringtone, false));
        list.add(new AlarmSetting(getString(R.string.beep_ringtone), R.raw.beep_ringtone, false));
        list.add(new AlarmSetting(getString(R.string.bell_ringtone), R.raw.bell_ringtone, false));

        setItemToTrue(list, alarmCreating.getNameRingtoneAlarm());
        return list;
    }

    private List<AlarmSetting> getListRepeatSelection() {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting(getString(R.string.once), false));
        list.add(new AlarmSetting(getString(R.string.daily), false));
        list.add(new AlarmSetting(getString(R.string.mon_to_fri), false));
        list.add(new AlarmSetting(getString(R.string.custom), false));

        setItemToTrue(list, alarmCreating.getRepeatModeAlarm());
        return list;
    }

    private void setItemToTrue(List<AlarmSetting> list, String str) {
        if (adjustExistingAlarm) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getNameItem().equals(str)) {
                    list.get(i).setSelected(true);
                }
            }
        } else {
            list.get(0).setSelected(true);
        }
    }

    private void initComponents() {
        btnClose = findViewById(R.id.btn_close_set_alarm);
        btnSaveAlarm = findViewById(R.id.btn_save_set_alarm);
        tvHeader = findViewById(R.id.tv_header_set_alarm);
        tvHeader.setText(getIntent().getStringExtra("title_alarm_activity"));
        tvTitle = findViewById(R.id.tv_title_set_alarm);
        timePicker = findViewById(R.id.time_picker_set_alarm);
        recyclerView = findViewById(R.id.rcv_set_alarm);
    }

    private void updateUIWithAlarmSetting(Alarm alarm) {
        TimePickerUtil.setHourTimePicker(timePicker, alarm.getAlarmHour());
        TimePickerUtil.setMinuteTimePicker(timePicker, alarm.getAlarmMinute());
        tvTitle.setText(getTitleTimeDuration());
    }

    private void setDefaultAlarm() {
        alarmCreating.setAlarmId(new Random().nextInt(Integer.MAX_VALUE));
        alarmCreating.setTitleAlarm("");
        alarmCreating.setAlarmHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarmCreating.setAlarmMinute(calendar.get(Calendar.MINUTE));
        alarmCreating.setRingToneAlarm(R.raw.default_ringtone);
        alarmCreating.setNameRingtoneAlarm(getString(R.string.default_setting));
        alarmCreating.setRepeatModeAlarm(getString(R.string.once));
        alarmCreating.setRecurring(false);
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

    private void setWeekdayAlarm() {
        alarmCreating.setMonday(true);
        alarmCreating.setTuesday(true);
        alarmCreating.setWednesday(true);
        alarmCreating.setThursday(true);
        alarmCreating.setFriday(true);
        alarmCreating.setSaturday(false);
        alarmCreating.setSunday(false);
    }

    private String getTitleTimeDuration(){
        return DateTimeInterval.getDateTimeInterval(alarmCreating, context);
    }
}