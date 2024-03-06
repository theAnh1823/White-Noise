package com.example.whitenoiseapplication.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SheetAudioAdapter;
import com.example.whitenoiseapplication.listener.IClickItemBottemSheet;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.Setting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheetAudio extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private SheetAudioAdapter sheetAudioAdapter;
    private ImageView imageView;
    private TextView textView;
    private Audio mAudio;
    private List<Setting> menuLists;
    private IClickItemBottemSheet iClickItemBottemSheet;
    public BottomSheetAudio(Audio mAudio, List<Setting> menuLists, IClickItemBottemSheet iClickItemBottemSheet) {
        this.mAudio = mAudio;
        this.menuLists = menuLists;
        this.iClickItemBottemSheet = iClickItemBottemSheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_audio, null);
        bottomSheetDialog.setContentView(view);

        imageView = view.findViewById(R.id.img_audio_sheet);
        Glide.with(getContext()).load(mAudio.getImageResource()).into(imageView);

        textView = view.findViewById(R.id.tv_name_audio);
        textView.setText(mAudio.getNameAudio());

        recyclerView = view.findViewById(R.id.rcv_sheet_audio);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        sheetAudioAdapter = new SheetAudioAdapter(menuLists, new IClickItemBottemSheet() {
            @Override
            public void onItemClick(Setting itemMenuList) {
                iClickItemBottemSheet.onItemClick(itemMenuList);
                sheetAudioAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(sheetAudioAdapter);
        return bottomSheetDialog;
    }
}
