package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.databinding.ItemSetAlarmBinding;
import com.example.whitenoiseapplication.listener.IClickItemBottomSheet;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SetAlarmAdapter extends RecyclerView.Adapter<SetAlarmAdapter.SetAlarmHolder>{
    private final Context context;
    private final List<Setting> listSetAlarm;
    private final IClickItemBottomSheet iClickItemBottomSheet;

    public SetAlarmAdapter(Context context, List<Setting> listSetAlarm, IClickItemBottomSheet iClickItemBottomSheet) {
        this.context = context;
        this.listSetAlarm = listSetAlarm;
        this.iClickItemBottomSheet = iClickItemBottomSheet;
    }

    @NonNull
    @Override
    public SetAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSetAlarmBinding binding = ItemSetAlarmBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SetAlarmHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAlarmHolder holder, int position) {
        Setting setAlarm = listSetAlarm.get(position);
        if (setAlarm != null){
            holder.binding.tvTitleItemSetAlarm.setText(setAlarm.getNameItem());
            holder.binding.tvContentItemSetAlarm.setText(setAlarm.getContentItem());
            holder.binding.layoutItemSetAlarm.setOnClickListener(v -> {
                iClickItemBottomSheet.onItemClick(setAlarm);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listSetAlarm != null){
            return listSetAlarm.size();
        }
        return 0;
    }

    public class SetAlarmHolder extends RecyclerView.ViewHolder{
        private final ItemSetAlarmBinding binding;

        public SetAlarmHolder(@NonNull ItemSetAlarmBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
