package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.SheetSetAlarmAdapter;
import com.example.whitenoiseapplication.listener.IClickItemByPosition;
import com.example.whitenoiseapplication.model.AlarmSetting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheetSetAlarm extends BottomSheetDialogFragment {
    private String titleSheet;
    private MediaPlayer mediaPlayer;
    private TextView tvTitleSheet;
    private RecyclerView recyclerView;
    private LinearLayout buttonLayout;
    private AppCompatButton btnSave, btnCancel;
    private SheetSetAlarmAdapter sheetSetAlarmAdapter;
    private List<AlarmSetting> attributeList;
    private IClickItemByPosition clickItemByPosition;

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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_set_alarm, null);
        sheetDialog.setContentView(view);

        mediaPlayer = new MediaPlayer();
        tvTitleSheet = view.findViewById(R.id.title_sheet_set_alarm);
        tvTitleSheet.setText(titleSheet);
        buttonLayout = view.findViewById(R.id.button_layout);
        if (titleSheet == getString(R.string.repeat_alarm)) {
            buttonLayout.setVisibility(View.GONE);
        }
        btnSave = view.findViewById(R.id.btn_save_alarm_setting);
        btnCancel = view.findViewById(R.id.btn_bottom_sheet_cancel);
        btnSave.setOnClickListener(v -> {
            if (saveListener != null) {
                saveListener.saveAlarmSetting();
            }
        });
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        recyclerView = view.findViewById(R.id.rcv_sheet_set_alarm);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        sheetSetAlarmAdapter = new SheetSetAlarmAdapter(getContext(), attributeList, new IClickItemByPosition() {
            @Override
            public void onItemClick(int position) {
                if (titleSheet == getString(R.string.alarm_sound)) {
                    playRingtone(attributeList.get(position).getResourceAudio());
                }
                clickItemByPosition.onItemClick(position);
            }
        });
        recyclerView.setAdapter(sheetSetAlarmAdapter);
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
