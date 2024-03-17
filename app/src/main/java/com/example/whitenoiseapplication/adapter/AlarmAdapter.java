package com.example.whitenoiseapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickItemAlarm;
import com.example.whitenoiseapplication.listener.OnSwitchCompatListener;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.util.DayIdsStringConverter;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;
import com.example.whitenoiseapplication.viewmodel.CreateAlarmViewModel;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context context;
    private List<Alarm> listAlarm;
    private IClickItemAlarm iClickItemAlarm;
    private OnSwitchCompatListener switchCompatListener;
    private CreateAlarmViewModel createAlarmViewModel;
    private AlarmsListViewModel alarmsListViewModel;

    public AlarmAdapter(Context context, CreateAlarmViewModel createAlarmViewModel, AlarmsListViewModel alarmsListViewModel, List<Alarm> listAlarm, IClickItemAlarm iClickItemAlarm, OnSwitchCompatListener switchCompatListener) {
        this.context = context;
        this.createAlarmViewModel = createAlarmViewModel;
        this.alarmsListViewModel = alarmsListViewModel;
        this.listAlarm = listAlarm;
        this.iClickItemAlarm = iClickItemAlarm;
        this.switchCompatListener = switchCompatListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = listAlarm.get(position);
        if (alarm != null) {
            holder.tvAlarmTime.setText(String.format("%02d", alarm.getAlarmHour()) + ":" + String.format("%02d", alarm.getAlarmMinute()));

            String contentItemAlarm = "";
            if (alarm.isRepeatForDaysOfWeek()) {
                contentItemAlarm = DayIdsStringConverter.getStringDaysOfWeek(alarm, context);
            } else {
                contentItemAlarm = context.getString(alarm.getRepeatModeId());
            }
            if (!alarm.getTitleAlarm().isEmpty()) {
                contentItemAlarm += " | " + alarm.getTitleAlarm();
            }
            holder.tvContentAlarm.setText(contentItemAlarm);
            changeTextColor(alarm, holder);

            holder.layoutItemAlarm.setOnClickListener(v -> {
                iClickItemAlarm.onClickItemAlarm(alarm);
            });

            holder.switchEnableAlarm.setChecked(alarm.isAlarmEnabled());
            holder.switchEnableAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    private void changeTextColor(Alarm alarm, AlarmViewHolder holder) {
        if (alarm.isAlarmEnabled()) {
            holder.tvAlarmTime.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tvContentAlarm.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.tvAlarmTime.setTextColor(ContextCompat.getColor(context, R.color.medium_gray));
            holder.tvContentAlarm.setTextColor(ContextCompat.getColor(context, R.color.medium_gray));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListAlarm(List<Alarm> listAlarm) {
        this.listAlarm = listAlarm;
        notifyDataSetChanged();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layoutItemAlarm;
        private TextView tvAlarmTime, tvContentAlarm;
        private SwitchCompat switchEnableAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemAlarm = itemView.findViewById(R.id.layout_item_alarm);
            tvAlarmTime = itemView.findViewById(R.id.alarm_time);
            tvContentAlarm = itemView.findViewById(R.id.content_item_alarm);
            switchEnableAlarm = itemView.findViewById(R.id.switch_enable_alarm);
        }
    }

    public void removeItem(Alarm alarm, int index) {
        alarmsListViewModel.delete(alarm);
        listAlarm.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(Alarm alarm, int index) {
        createAlarmViewModel.insert(alarm);
        alarm.schedule(context);
        listAlarm.add(index, alarm);
        notifyItemInserted(index);
    }
}
