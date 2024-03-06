package com.example.whitenoiseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.listener.IClickChangeLanguage;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter {
    private List<Setting> mListSetting;
    private IClickItemByPosition iClickItemByPosition;
    private IClickChangeLanguage iClickChangeLanguage;

    public SettingAdapter(List<Setting> list, IClickItemByPosition iClickItemByPosition, IClickChangeLanguage iClickChangeLanguage) {
        this.mListSetting = list;
        this.iClickItemByPosition = iClickItemByPosition;
        this.iClickChangeLanguage = iClickChangeLanguage;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mListSetting.get(position).getViewType()) {
            case 1:
                return Setting.layoutItemSetting;
            case 2:
                return Setting.layoutItemLanguage;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View layoutSetting = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
                return new SettingViewHolder(layoutSetting);
            case 2:
                View layoutLanguage = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_language, parent, false);
                return new ChangeLanguageHolder(layoutLanguage);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Setting setting = mListSetting.get(position);
        if (setting == null) {
            return;
        }
        if (holder instanceof SettingViewHolder) {
            ((SettingViewHolder) holder).tvSetting.setText(setting.getNameItem());
            ((SettingViewHolder) holder).imageView.setImageResource(setting.getImageResource());
            ((SettingViewHolder) holder).layoutSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        iClickItemByPosition.onItemClick(adapterPosition);
                    }
                }
            });
        } else if (holder instanceof ChangeLanguageHolder) {
            ((ChangeLanguageHolder) holder).switchLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    iClickChangeLanguage.clickChangeLanguage(isChecked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mListSetting != null) {
            return mListSetting.size();
        }
        return 0;
    }

    public static class SettingViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutSetting;
        private ImageView imageView;
        private TextView tvSetting;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSetting = itemView.findViewById(R.id.layout_item_setting);
            imageView = itemView.findViewById(R.id.img_item_setting);
            tvSetting = itemView.findViewById(R.id.tv_name_setting);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public class ChangeLanguageHolder extends RecyclerView.ViewHolder {
        private TextView tvLanguage;
        private SwitchCompat switchLanguage;

        public ChangeLanguageHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguage = itemView.findViewById(R.id.content_item_language);
            switchLanguage = itemView.findViewById(R.id.switch_change_language);
        }
    }
}
