package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SheetSetAlarmAdapter;
import com.example.whitenoiseapplication.databinding.LayoutSheetSetAlarmBinding;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.AlarmSetting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheetSetAlarm extends BottomSheetDialogFragment {
    private final String titleSheet;
    private MediaPlayer mediaPlayer;
    private final List<AlarmSetting> attributeList;
    private final IClickItemByPosition clickItemByPosition;

    public interface SaveListener {
        void saveAlarmSetting();
    }

    private SaveListener saveListener;

    public BottomSheetSetAlarm(String titleSheet, List<AlarmSetting> attributeList, IClickItemByPosition clickItemByPosition) {
        this.titleSheet = titleSheet;
        this.attributeList = attributeList;
        this.clickItemByPosition = clickItemByPosition;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog sheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        LayoutSheetSetAlarmBinding binding = LayoutSheetSetAlarmBinding.inflate(LayoutInflater.from(getContext()));
        sheetDialog.setContentView(binding.getRoot());

        mediaPlayer = new MediaPlayer();
        binding.titleSheetSetAlarm.setText(titleSheet);
        if (titleSheet.equals(getString(R.string.repeat_alarm))) {
            binding.buttonLayout.setVisibility(View.GONE);
        }
        binding.btnSaveAlarmSetting.setOnClickListener(v -> {
            if (saveListener != null) {
                saveListener.saveAlarmSetting();
            }
        });
        binding.btnCancel.setOnClickListener(v -> dismiss());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvSheetSetAlarm.setLayoutManager(linearLayoutManager);
        SheetSetAlarmAdapter sheetSetAlarmAdapter = new SheetSetAlarmAdapter(getContext(), attributeList, (int position) -> {
            if (titleSheet.equals(getString(R.string.alarm_sound))) {
                playRingtone(attributeList.get(position).getResourceAudio());
            }
            clickItemByPosition.onItemClick(position);
        });
        binding.rcvSheetSetAlarm.setAdapter(sheetSetAlarmAdapter);
        return sheetDialog;
    }

    private void playRingtone(int resourceRingtone) {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(getContext(), resourceRingtone);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    public void setSaveListener(SaveListener listener) {
        saveListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDismiss(dialog);
    }
}
