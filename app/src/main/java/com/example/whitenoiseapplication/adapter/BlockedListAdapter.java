package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ItemBlockedListBinding;
import com.example.whitenoiseapplication.listener.IClickItemAudioListener;
import com.example.whitenoiseapplication.model.Audio;

import java.util.List;

public class BlockedListAdapter extends RecyclerView.Adapter<BlockedListAdapter.BlockedHolder>{
    private Context context;
    private List<Audio> mList;
    private IClickItemAudioListener listener;

    public BlockedListAdapter(Context context, List<Audio> mList, IClickItemAudioListener listener) {
        this.context = context;
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlockedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlockedListBinding binding = ItemBlockedListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BlockedHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedHolder holder, int position) {
        Audio audio = mList.get(position);
        if (audio != null){
            holder.binding.tvNameAudio.setText(audio.getNameAudio());
            Glide.with(context).load(audio.getImageResource()).into(holder.binding.imgItemBlocked);
            holder.binding.btnUnblocked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickItemAudio(audio);
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

    public static class BlockedHolder extends RecyclerView.ViewHolder {
        private final ItemBlockedListBinding binding;
        public BlockedHolder(@NonNull ItemBlockedListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
