package com.example.whitenoiseapplication.adapter;

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

public class FavAudioAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private List<Audio> mListAudio;
    private List<Audio> mListAudioOld;
    private static IClickItemAudioListener iClickItemAudioListener;

    public FavAudioAdapter(Context context, List<Audio> mListAudio, IClickItemAudioListener listener) {
        this.context = context;
        this.mListAudio = mListAudio;
        this.mListAudioOld = mListAudio;
        this.iClickItemAudioListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Audio audio = mListAudio.get(position);
        return audio.getTypeDisplay();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Audio.TYPE_GRID:
                View viewGrid = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_grid, parent, false);
                return new FavAudioHolder(viewGrid);
            case Audio.TYPE_LIST:
                View viewList = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_list, parent, false);
                return new FavAudioHolder(viewList);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Audio audio = mListAudio.get(position);
        if (audio == null) {
            return;
        }

        if(holder instanceof FavAudioHolder) {
            ((FavAudioHolder) holder).tvAudio.setText(audio.getNameAudio());
            Glide.with(context).load(audio.getImageResource()).into(((FavAudioHolder) holder).imgAudio);
            if (audio.isBlocked()) {
                int color = ContextCompat.getColor(context, R.color.medium_gray);
                ((FavAudioHolder) holder).tvAudio.setTextColor(color);
                ((FavAudioHolder) holder).btnMore.setImageResource(R.drawable.block_white);
            } else {
                int color = ContextCompat.getColor(context, R.color.white);
                ((FavAudioHolder) holder).tvAudio.setTextColor(color);
                ((FavAudioHolder) holder).btnMore.setImageResource(R.drawable.more_vert_white);
            }
            ((FavAudioHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickItemAudioListener.onClickMore(audio);
                }
            });

            if (audio.isFavorite()) {
                ((FavAudioHolder) holder).imgFavorite.setVisibility(View.VISIBLE);
                ((FavAudioHolder) holder).imgFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iClickItemAudioListener.onClickFavorite(audio);
                    }
                });
            } else {
                ((FavAudioHolder) holder).imgFavorite.setVisibility(View.GONE);
            }

            ((FavAudioHolder) holder).layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickItemAudioListener.onClickItemAudio(audio);
                }
            });
        }
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

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListAudio = (List<Audio>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class FavAudioHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutItem;
        private ImageView imgAudio, imgFavorite;
        private TextView tvAudio;
        private ImageButton btnMore;

        public FavAudioHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgAudio = itemView.findViewById(R.id.img_item_home);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            tvAudio = itemView.findViewById(R.id.tv_name_audio);
            btnMore = itemView.findViewById(R.id.img_btn_more);
        }
    }
}
