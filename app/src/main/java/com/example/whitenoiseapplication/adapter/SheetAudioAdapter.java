package com.example.whitenoiseapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.databinding.ItemBottomSheetBinding;
import com.example.whitenoiseapplication.listener.IClickItemBottomSheet;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SheetAudioAdapter extends RecyclerView.Adapter<SheetAudioAdapter.BottomSheetHolder>{
    private final List<Setting> mList;
    private final IClickItemBottomSheet iClickItemBottomSheet;

    public SheetAudioAdapter(List<Setting> mList, IClickItemBottomSheet iClickItemBottomSheet) {
        this.mList = mList;
        this.iClickItemBottomSheet = iClickItemBottomSheet;
    }

    @NonNull
    @Override
    public BottomSheetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBottomSheetBinding binding = ItemBottomSheetBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BottomSheetHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetHolder holder, int position) {
        Setting itemBottomSheet = mList.get(position);
        if (itemBottomSheet != null){
            holder.binding.imgItemSheet.setImageResource(itemBottomSheet.getImageResource());
            holder.binding.tvNameSheet.setText(itemBottomSheet.getNameItem());
            holder.binding.itemLayoutSheet.setOnClickListener(v -> {
                iClickItemBottomSheet.onItemClick(itemBottomSheet);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null){
            return mList.size();
        }
        return 0;
    }

    public static class BottomSheetHolder extends RecyclerView.ViewHolder{
        private final ItemBottomSheetBinding binding;
        public BottomSheetHolder(@NonNull ItemBottomSheetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
