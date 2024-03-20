package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.adapter.SheetAudioAdapter;
import com.example.whitenoiseapplication.databinding.LayoutSheetAudioBinding;
import com.example.whitenoiseapplication.listener.IClickItemBottomSheet;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.Setting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheetAudio extends BottomSheetDialogFragment {
    private SheetAudioAdapter sheetAudioAdapter;
    private final Audio mAudio;
    private final List<Setting> menuLists;
    private final IClickItemBottomSheet iClickItemBottomSheet;
    public BottomSheetAudio(Audio mAudio, List<Setting> menuLists, IClickItemBottomSheet iClickItemBottomSheet) {
        this.mAudio = mAudio;
        this.menuLists = menuLists;
        this.iClickItemBottomSheet = iClickItemBottomSheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        LayoutSheetAudioBinding binding = LayoutSheetAudioBinding.inflate(inflater);
        bottomSheetDialog.setContentView(binding.getRoot());

        Glide.with(requireContext()).load(mAudio.getImageResource()).into(binding.imgAudioSheet);

        binding.tvNameAudio.setText(mAudio.getNameAudio());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvSheetAudio.setLayoutManager(linearLayoutManager);

        sheetAudioAdapter = new SheetAudioAdapter(menuLists, new IClickItemBottomSheet() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(Setting itemMenuList) {
                iClickItemBottomSheet.onItemClick(itemMenuList);
                sheetAudioAdapter.notifyDataSetChanged();
            }
        });
        binding.rcvSheetAudio.setAdapter(sheetAudioAdapter);
        return bottomSheetDialog;
    }
}
