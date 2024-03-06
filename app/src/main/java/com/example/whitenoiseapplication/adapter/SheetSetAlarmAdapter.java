package com.example.whitenoiseapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.AlarmSetting;

import java.util.List;

public class SheetSetAlarmAdapter extends RecyclerView.Adapter<SheetSetAlarmAdapter.SetAlarmHolder> {
    private Context context;
    private List<AlarmSetting> mList;
    private IClickItemByPosition clickItemByPosition;

    public SheetSetAlarmAdapter(Context context, List<AlarmSetting> mList, IClickItemByPosition clickItemByPosition) {
        this.context = context;
        this.mList = mList;
        this.clickItemByPosition = clickItemByPosition;
    }

    @NonNull
    @Override
    public SetAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
        return new SetAlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAlarmHolder holder, @SuppressLint("RecyclerView") int position) {
        AlarmSetting itemSheet = mList.get(position);
        if (itemSheet != null) {
            holder.tvBottemSheet.setText(itemSheet.getNameItem());
            holder.tvBottemSheet.setTextColor(ContextCompat.getColor(context, R.color.black));
            if (itemSheet.isSelected()) {
                holder.layoutBottemSheet.setBackgroundResource(R.color.pale_turquoise);
                holder.tvBottemSheet.setTextColor(ContextCompat.getColor(context, R.color.navy));
                holder.imgSelectedItem.setVisibility(View.VISIBLE);
                holder.imgSelectedItem.setImageResource(R.drawable.done_21dp_navy);
            } else {
                holder.layoutBottemSheet.setBackgroundResource(R.color.white);
                holder.tvBottemSheet.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.imgSelectedItem.setVisibility(View.GONE);
            }
            holder.layoutBottemSheet.setOnClickListener(new View.OnClickListener() {
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
        private ConstraintLayout layoutBottemSheet;
        private TextView tvBottemSheet;
        private ImageView imgSelectedItem;

        public SetAlarmHolder(@NonNull View itemView) {
            super(itemView);
            layoutBottemSheet = itemView.findViewById(R.id.layout_item_setting);
            tvBottemSheet = itemView.findViewById(R.id.tv_name_setting);
            imgSelectedItem = itemView.findViewById(R.id.img_attribute_item_setting);
        }
    }
}
