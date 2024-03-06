package com.example.whitenoiseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickItemBottemSheet;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SheetAudioAdapter extends RecyclerView.Adapter<SheetAudioAdapter.BottemSheetHolder>{
    private List<Setting> mList;
    private IClickItemBottemSheet iClickItemBottemSheet;

    public SheetAudioAdapter(List<Setting> mList, IClickItemBottemSheet iClickItemBottemSheet) {
        this.mList = mList;
        this.iClickItemBottemSheet = iClickItemBottemSheet;
    }

    @NonNull
    @Override
    public BottemSheetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet,parent,false);
        return new BottemSheetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottemSheetHolder holder, int position) {
        Setting itemBottemSheet = mList.get(position);
        if (itemBottemSheet != null){
            holder.imageView.setImageResource(itemBottemSheet.getImageResource());
            holder.tvBottemSheet.setText(itemBottemSheet.getNameItem());
            holder.layoutBottemSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        iClickItemBottemSheet.onItemClick(itemBottemSheet);
                }
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

    public static class BottemSheetHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout layoutBottemSheet;
        private ImageView imageView;
        private TextView tvBottemSheet;
        public BottemSheetHolder(@NonNull View itemView) {
            super(itemView);
            layoutBottemSheet = itemView.findViewById(R.id.item_layout_sheet);
            imageView = itemView.findViewById(R.id.img_item_sheet);
            tvBottemSheet = itemView.findViewById(R.id.tv_name_sheet);
        }
    }
}
