package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Sleep;

import java.util.List;

public class SleepCalculatorAdapter extends RecyclerView.Adapter<SleepCalculatorAdapter.SleepCalculatorHolder>{
    private Context context;
    private List<Sleep> mList;

    public SleepCalculatorAdapter(Context context, List<Sleep> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public SleepCalculatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_result_sleep_calculator, parent, false);
        return new SleepCalculatorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepCalculatorHolder holder, int position) {
        Sleep sleep = mList.get(position);
        if (sleep != null){
            holder.tvHour.setText(sleep.getHourSleep());
            holder.tvInfo.setText(sleep.getInfoItem());
            if (position > 1)
                holder.tvSuggested.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null){
            return mList.size();
        }
        return 0;
    }

    public class SleepCalculatorHolder extends RecyclerView.ViewHolder{
        private TextView tvHour;
        private TextView tvInfo;
        private TextView tvSuggested;
        public SleepCalculatorHolder(@NonNull View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tv_result_sleep_calculator);
            tvSuggested = itemView.findViewById(R.id.tv_suggested);
            tvInfo = itemView.findViewById(R.id.info_item_result_sleep);
        }
    }
}
