package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.LayoutSheetAddLabelBinding;
import com.example.whitenoiseapplication.model.Setting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetAddLabelAlarm extends BottomSheetDialogFragment {
    private final Setting mSetting;
    public BottomSheetAddLabelAlarm(Setting setting) {
        mSetting = setting;
    }

    public interface AddLabelListener {
        void onAddLabelButtonClicked(String labelText);
    }

    private AddLabelListener mListener;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog sheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        LayoutSheetAddLabelBinding binding = LayoutSheetAddLabelBinding.inflate(inflater);
        sheetDialog.setContentView(binding.getRoot());

        binding.editTextLabel.setText(mSetting.getContentItem());

        binding.btnAddLabel.setOnClickListener(v -> {
            if (mListener != null) {
                if (binding.editTextLabel.getText().toString().length() > 30)
                    Toast.makeText(getContext(), R.string.warning_length_label, Toast.LENGTH_LONG).show();
                else
                    mListener.onAddLabelButtonClicked(binding.editTextLabel.getText().toString());
            }
        });
        binding.btnDialogCancel.setOnClickListener(v -> {
            dismiss();
        });

        return sheetDialog;
    }

    public void setAddLabelListener(AddLabelListener listener) {
        mListener = listener;
    }
}
