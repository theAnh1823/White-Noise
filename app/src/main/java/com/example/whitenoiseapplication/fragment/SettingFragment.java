package com.example.whitenoiseapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.activity.BlockedListActivity;
import com.example.whitenoiseapplication.activity.PolicyActivity;
import com.example.whitenoiseapplication.adapter.SettingAdapter;
import com.example.whitenoiseapplication.databinding.SettingFragmentBinding;
import com.example.whitenoiseapplication.model.Setting;
import com.example.whitenoiseapplication.util.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SettingFragmentBinding binding = SettingFragmentBinding.inflate(inflater, container, false);
        SettingAdapter mSettingAdapter = new SettingAdapter(getContext(), getListSetting(), position -> {
            switch (position) {
                case 0:
                    openBlockedList();
                    break;
                case 1:
                    manageNotifications();
                    break;
                case 2:
                    shareApp();
                    break;
                case 3:
                    openPolicyActivity();
                    break;
                case 4:
                    sendFeedback();
                    break;
            }
        }, isChecked -> {
            if (isChecked) {
                context = LocaleHelper.setLocale(getContext(), "vi");
            } else {
                context = LocaleHelper.setLocale(getContext(), "en");
            }
            startMainActivity();
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvSetting.setLayoutManager(linearLayoutManager);
        binding.rcvSetting.setAdapter(mSettingAdapter);
        return binding.getRoot();
    }

    private void startMainActivity() {
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }

    private void openBlockedList() {
        Intent intent = new Intent(getActivity(), BlockedListActivity.class);
        startActivity(intent);
    }

    private void manageNotifications() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", requireActivity().getPackageName());
            intent.putExtra("app_uid", requireActivity().getApplicationInfo().uid);
        }
        requireActivity().startActivity(intent);
    }

    private void openPolicyActivity() {
        Intent intent = new Intent(getActivity(), PolicyActivity.class);
        startActivity(intent);
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_app));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void sendFeedback() {
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_to_receive_feedback)});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_feedback_about_app));
        startActivity(Intent.createChooser(feedbackEmail, getString(R.string.send_feedback)));
    }

    private List<Setting> getListSetting() {
        List<Setting> mList = new ArrayList<>();
        mList.add(new Setting(1, R.drawable.block_white, getString(R.string.blocked_list)));
        mList.add(new Setting(1, R.drawable.notifications, getString(R.string.manage_notifications)));
        mList.add(new Setting(1, R.drawable.share, getString(R.string.share)));
        mList.add(new Setting(1, R.drawable.policy, getString(R.string.privacy_policy)));
        mList.add(new Setting(1, R.drawable.feedback, getString(R.string.feedback)));
        mList.add(new Setting(2, R.drawable.language, getString(R.string.languages), getString(R.string.current_language)));

        return mList;
    }
}
