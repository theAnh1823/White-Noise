package com.example.whitenoiseapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickItemAudioListener;
import com.example.whitenoiseapplication.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> implements Filterable {
    private final Context context;
    private List<Audio> mListAudio;
    private final List<Audio> mListAudioOld;
    private static IClickItemAudioListener iClickItemAudioListener;

    public AudioAdapter(Context context, List<Audio> mListAudio, IClickItemAudioListener listener) {
        this.context = context;
        this.mListAudio = mListAudio;
        this.mListAudioOld = mListAudio;
        iClickItemAudioListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Audio audio = mListAudio.get(position);
        return audio.getTypeDisplay();
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Audio.TYPE_GRID:
                View viewGrid = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_grid, parent, false);
                return new AudioViewHolder(viewGrid);
            case Audio.TYPE_LIST:
                View viewList = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_list, parent, false);
                return new AudioViewHolder(viewList);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Audio audio = mListAudio.get(position);
        if (audio == null) {
            return;
        }

        holder.tvAudio.setText(audio.getNameAudio());
        Glide.with(context).load(audio.getImageResource()).into(holder.imgAudio);
        if (audio.isBlocked()) {
            int color = ContextCompat.getColor(context, R.color.medium_gray);
            holder.tvAudio.setTextColor(color);
            holder.btnMore.setImageResource(R.drawable.block_white);
        } else {
            int color = ContextCompat.getColor(context, R.color.white);
            holder.tvAudio.setTextColor(color);
            holder.btnMore.setImageResource(R.drawable.more_vert_white);
        }
        holder.btnMore.setOnClickListener(v -> {
            iClickItemAudioListener.onClickMore(audio);
        });

        if (audio.isFavorite()) {
            holder.imgFavorite.setVisibility(View.VISIBLE);
            holder.imgFavorite.setOnClickListener(v -> iClickItemAudioListener.onClickFavorite(audio));
        } else {
            holder.imgFavorite.setVisibility(View.GONE);
        }
        holder.layoutItem.setOnClickListener(v -> iClickItemAudioListener.onClickItemAudio(audio));
    }

    @Override
    public int getItemCount() {
        if (mListAudio != null) {
            return mListAudio.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String stringSearch = constraint.toString();
                if (stringSearch.isEmpty()) {
                    mListAudio = mListAudioOld;
                } else {
                    stringSearch = ".*" + stringSearch.toLowerCase() + ".*";
                    List<Audio> list = new ArrayList<>();
                    for (Audio audio : mListAudioOld) {
                        if (audio.getNameAudio().toLowerCase().matches(stringSearch)) {
                            list.add(audio);
                        }
                    }
                    mListAudio = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mListAudio;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListAudio = (List<Audio>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layoutItem;
        private final ImageView imgAudio;
        private final ImageView imgFavorite;
        private final TextView tvAudio;
        private final ImageButton btnMore;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgAudio = itemView.findViewById(R.id.img_item_home);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvAudio = itemView.findViewById(R.id.tv_name_audio);
            btnMore = itemView.findViewById(R.id.img_btn_more);
        }
    }
}
