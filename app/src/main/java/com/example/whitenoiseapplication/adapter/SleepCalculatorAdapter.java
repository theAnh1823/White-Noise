package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.databinding.ItemResultSleepCalculatorBinding;
import com.example.whitenoiseapplication.model.Sleep;

import java.util.List;

public class SleepCalculatorAdapter extends RecyclerView.Adapter<SleepCalculatorAdapter.SleepCalculatorHolder>{
    private final Context context;
    private final List<Sleep> mList;

    public SleepCalculatorAdapter(Context context, List<Sleep> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public SleepCalculatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResultSleepCalculatorBinding binding = ItemResultSleepCalculatorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SleepCalculatorHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepCalculatorHolder holder, int position) {
        Sleep sleep = mList.get(position);
        if (sleep != null){
            holder.binding.tvResult.setText(sleep.getHourSleep());
            holder.binding.tvInfoResultSleep.setText(sleep.getInfoItem());
            if (position > 1)
                holder.binding.tvSuggested.setVisibility(View.GONE);
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
        private final ItemResultSleepCalculatorBinding binding;
        public SleepCalculatorHolder(@NonNull ItemResultSleepCalculatorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
