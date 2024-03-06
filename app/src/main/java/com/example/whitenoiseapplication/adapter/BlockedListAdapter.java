package com.example.whitenoiseapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocked_list, parent, false);
        return new BlockedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedHolder holder, int position) {
        Audio audio = mList.get(position);
        if (audio != null){
            holder.tvItem.setText(audio.getNameAudio());
            Glide.with(context).load(audio.getImageResource()).into(holder.imageView);
            holder.btnUnBlocked.setOnClickListener(new View.OnClickListener() {
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
        private ImageView imageView;
        private Button btnUnBlocked;
        private TextView tvItem;
        public BlockedHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_item_blocked);
            btnUnBlocked = itemView.findViewById(R.id.btn_unblocked);
            tvItem = itemView.findViewById(R.id.tv_name_audio);
        }
    }
}
