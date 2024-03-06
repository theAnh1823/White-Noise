package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.model.Setting;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetAddLabelAlarm extends BottomSheetDialogFragment {
    private AppCompatButton btnClose;
    private AppCompatButton btnAddLabel;
    private TextInputEditText textLabel;
    private Setting mSetting;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_sheet_add_label, null);
        sheetDialog.setContentView(view);

        btnAddLabel = view.findViewById(R.id.btn_add_label);
        btnClose = view.findViewById(R.id.btn_dialog_cancel);
        textLabel = view.findViewById(R.id.edit_text_label);
        textLabel.setText(mSetting.getContentItem());

        btnAddLabel.setOnClickListener(v -> {
            if (mListener != null) {
                if (textLabel.getText().toString().length() > 30)
                    Toast.makeText(getContext(), R.string.warning_length_label, Toast.LENGTH_LONG).show();
                else
                    mListener.onAddLabelButtonClicked(textLabel.getText().toString());
            }
        });
        btnClose.setOnClickListener(v -> {
            dismiss();
        });

        return sheetDialog;
    }

    public void setAddLabelListener(AddLabelListener listener) {
        mListener = listener;
    }
}
