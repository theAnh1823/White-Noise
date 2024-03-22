package com.example.whitenoiseapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ItemSettingBinding;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.AlarmSetting;

import java.util.List;

public class SheetSetAlarmAdapter extends RecyclerView.Adapter<SheetSetAlarmAdapter.SetAlarmHolder> {
    private final Context context;
    private final List<AlarmSetting> mList;
    private final IClickItemByPosition clickItemByPosition;

    public SheetSetAlarmAdapter(Context context, List<AlarmSetting> mList, IClickItemByPosition clickItemByPosition) {
        this.context = context;
        this.mList = mList;
        this.clickItemByPosition = clickItemByPosition;
    }

    @NonNull
    @Override
    public SetAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSettingBinding binding = ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SetAlarmHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAlarmHolder holder, @SuppressLint("RecyclerView") int position) {
        AlarmSetting itemSheet = mList.get(position);
        if (itemSheet != null) {
            holder.binding.tvNameSetting.setText(context.getString(itemSheet.getIdName()));
            holder.binding.tvNameSetting.setTextColor(ContextCompat.getColor(context, R.color.black));
            if (itemSheet.isSelected()) {
                holder.binding.layoutItemSetting.setBackgroundResource(R.color.pale_turquoise);
                holder.binding.tvNameSetting.setTextColor(ContextCompat.getColor(context, R.color.navy));
                holder.binding.imgAttributeItemSetting.setVisibility(View.VISIBLE);
                holder.binding.imgAttributeItemSetting.setImageResource(R.drawable.done_21dp_navy);
            } else {
                holder.binding.layoutItemSetting.setBackgroundResource(R.color.white);
                holder.binding.tvNameSetting.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.binding.imgAttributeItemSetting.setVisibility(View.GONE);
            }
            holder.binding.layoutItemSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.get(position).setSelected(true);
                    for (int i = 0; i < mList.size(); i++) {
                        if (i != position) {
                            mList.get(i).setSelected(false);
                            notifyItemChanged(i);
                        }
                    }
                    notifyItemChanged(position);
                    clickItemByPosition.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public static class SetAlarmHolder extends RecyclerView.ViewHolder {
        private final ItemSettingBinding binding;

        public SetAlarmHolder(@NonNull ItemSettingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
