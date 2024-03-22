package com.example.whitenoiseapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ItemAlarmBinding;
import com.example.whitenoiseapplication.listener.IClickItemAlarm;
import com.example.whitenoiseapplication.listener.OnSwitchCompatListener;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.util.DayIdsStringConverter;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private final Context context;
    private List<Alarm> listAlarm;
    private final IClickItemAlarm iClickItemAlarm;
    private final OnSwitchCompatListener switchCompatListener;
    private final AlarmsListViewModel alarmsListViewModel;

    public AlarmAdapter(Context context, AlarmsListViewModel alarmsListViewModel, List<Alarm> listAlarm, IClickItemAlarm iClickItemAlarm, OnSwitchCompatListener switchCompatListener) {
        this.context = context;
        this.alarmsListViewModel = alarmsListViewModel;
        this.listAlarm = listAlarm;
        this.iClickItemAlarm = iClickItemAlarm;
        this.switchCompatListener = switchCompatListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlarmBinding binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AlarmViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = listAlarm.get(position);
        if (alarm != null) {
            holder.itemAlarmBinding.tvAlarmTime.setText(String.format("%02d", alarm.getAlarmHour()) + ":" + String.format("%02d", alarm.getAlarmMinute()));

            String contentItemAlarm;
            if (alarm.isRepeatForDaysOfWeek()) {
                contentItemAlarm = DayIdsStringConverter.getStringDaysOfWeek(alarm, context);
            } else {
                contentItemAlarm = context.getString(alarm.getRepeatModeId());
            }
            if (!alarm.getTitleAlarm().isEmpty()) {
                contentItemAlarm += " | " + alarm.getTitleAlarm();
            }
            holder.itemAlarmBinding.tvContentItemAlarm.setText(contentItemAlarm);
            changeTextColor(alarm, holder);

            holder.itemAlarmBinding.layoutItemAlarm.setOnClickListener(v -> iClickItemAlarm.onClickItemAlarm(alarm));

            holder.itemAlarmBinding.switchEnableAlarm.setChecked(alarm.isAlarmEnabled());
            holder.itemAlarmBinding.switchEnableAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switchCompatListener.onClickSwitchCompat(alarm);
                    changeTextColor(alarm, holder);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listAlarm != null) {
            return listAlarm.size();
        }
        return 0;
    }

    private void changeTextColor(@NonNull Alarm alarm, AlarmViewHolder holder) {
        if (alarm.isAlarmEnabled()) {
            holder.itemAlarmBinding.tvAlarmTime.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.itemAlarmBinding.tvContentItemAlarm.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemAlarmBinding.tvAlarmTime.setTextColor(ContextCompat.getColor(context, R.color.medium_gray));
            holder.itemAlarmBinding.tvContentItemAlarm.setTextColor(ContextCompat.getColor(context, R.color.medium_gray));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListAlarm(List<Alarm> listAlarm) {
        this.listAlarm = listAlarm;
        notifyDataSetChanged();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        public ItemAlarmBinding itemAlarmBinding;

        public AlarmViewHolder(@NonNull ItemAlarmBinding itemAlarmBinding) {
            super(itemAlarmBinding.getRoot());
            this.itemAlarmBinding = itemAlarmBinding;
        }
    }

    public void removeItem(Alarm alarm, int index) {
        alarmsListViewModel.delete(alarm);
        listAlarm.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(Alarm alarm, int index) {
        alarmsListViewModel.insert(alarm);
        alarm.schedule(context);
        listAlarm.add(index, alarm);
        notifyItemInserted(index);
    }
}
