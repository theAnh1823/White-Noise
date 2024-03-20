package com.example.whitenoiseapplication.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.databinding.ItemSettingBinding;
import com.example.whitenoiseapplication.databinding.ItemSettingLanguageBinding;
import com.example.whitenoiseapplication.listener.IClickChangeLanguage;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.Setting;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final List<Setting> mListSetting;
    private final IClickItemByPosition iClickItemByPosition;
    private final IClickChangeLanguage iClickChangeLanguage;

    public SettingAdapter(Context context, List<Setting> list, IClickItemByPosition iClickItemByPosition, IClickChangeLanguage iClickChangeLanguage) {
        this.context = context;
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
                ItemSettingBinding itemSettingBinding = ItemSettingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new SettingViewHolder(itemSettingBinding);
            case 2:
                ItemSettingLanguageBinding itemSettingLanguageBinding = ItemSettingLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ChangeLanguageHolder(itemSettingLanguageBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Setting setting = mListSetting.get(position);
        if (setting == null) {
            return;
        }
        if (holder instanceof SettingViewHolder) {
            ((SettingViewHolder) holder).binding.tvNameSetting.setText(setting.getNameItem());
            ((SettingViewHolder) holder).binding.imgItemSetting.setVisibility(View.VISIBLE);
            ((SettingViewHolder) holder).binding.imgItemSetting.setImageResource(setting.getImageResource());
            ((SettingViewHolder) holder).binding.layoutItemSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        iClickItemByPosition.onItemClick(adapterPosition);
                    }
                }
            });
        } else if (holder instanceof ChangeLanguageHolder) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("pref_switch_language", MODE_PRIVATE);
            ((ChangeLanguageHolder) holder).binding.switchChangeLanguage.setChecked(sharedPreferences.getBoolean("value", false));
            ((ChangeLanguageHolder) holder).binding.switchChangeLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    iClickChangeLanguage.clickChangeLanguage(isChecked);

                    SharedPreferences.Editor editor = context.getSharedPreferences("pref_switch_language", MODE_PRIVATE).edit();
                    editor.putBoolean("value", isChecked);
                    editor.apply();
                    ((ChangeLanguageHolder) holder).binding.switchChangeLanguage.setChecked(isChecked);
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
        private final ItemSettingBinding binding;

        public SettingViewHolder(@NonNull ItemSettingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ChangeLanguageHolder extends RecyclerView.ViewHolder {
        private final ItemSettingLanguageBinding binding;

        public ChangeLanguageHolder(@NonNull ItemSettingLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
