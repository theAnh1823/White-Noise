package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickItemBottemSheet;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SetAlarmAdapter extends RecyclerView.Adapter<SetAlarmAdapter.SetAlarmHolder>{
    private Context context;
    private List<Setting> listSetAlarm;
    private IClickItemBottemSheet iClickItemBottemSheet;

    public SetAlarmAdapter(Context context, List<Setting> listSetAlarm, IClickItemBottemSheet iClickItemBottemSheet) {
        this.context = context;
        this.listSetAlarm = listSetAlarm;
        this.iClickItemBottemSheet = iClickItemBottemSheet;
    }

    @NonNull
    @Override
    public SetAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_set_alarm, parent, false);
        return new SetAlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAlarmHolder holder, int position) {
        Setting setAlarm = listSetAlarm.get(position);
        if (setAlarm != null){
            holder.tvTitleItemSetAlarm.setText(setAlarm.getNameItem());
            holder.tvContentItemSetAlarm.setText(setAlarm.getContentItem());
            holder.layoutItemSetAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickItemBottemSheet.onItemClick(setAlarm);
                }
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
        private ConstraintLayout layoutItemSetAlarm;
        private TextView tvTitleItemSetAlarm, tvContentItemSetAlarm;

        public SetAlarmHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemSetAlarm = itemView.findViewById(R.id.layout_item_set_alarm);
            tvTitleItemSetAlarm = itemView.findViewById(R.id.tv_title_item_set_alarm);
            tvContentItemSetAlarm = itemView.findViewById(R.id.tv_content_item_set_alarm);
        }
    }
}
