package com.example.whitenoiseapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.ViewPager2MainAdapter;
import com.example.whitenoiseapplication.databinding.ActivityMainBinding;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.CountDownManager;
import com.example.whitenoiseapplication.service.AudioService;
import com.example.whitenoiseapplication.util.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static ActivityMainBinding binding;
    private CountDownManager countDownManager;
    private Audio mAudio;
    private boolean isPlaying;
    private int actionAudio;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAudio = (Audio) bundle.get("object_audio");
                isPlaying = bundle.getBoolean("status_player");
                actionAudio = bundle.getInt("action_audio");

                handleLayoutAudio(actionAudio);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setElevation(0);

        binding.tvCountdownTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timer, 0, 0, 0);
        countDownManager = CountDownManager.initInstance(binding.tvCountdownTimer, () -> {
            sendActionToService(AudioService.ACTION_CLOSE);
            binding.tvCountdownTimer.setVisibility(View.GONE);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        Context context;
        if (isVietnameseLanguage) {
            context = LocaleHelper.setLocale(this, "vi");
        } else {
            context = LocaleHelper.setLocale(this, "en");
        }

        binding.viewPager2.setUserInputEnabled(false);
        setUpViewPager2();

        binding.bottomNav.getMenu().clear();
        binding.bottomNav.inflateMenu(R.menu.menu_bottom_nav);
        binding.bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint({"ResourceType", "NonConstantResourceId"})
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.viewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.action_sleep_calculator:
                        binding.viewPager2.setCurrentItem(1, false);
                        break;
                    case R.id.action_alarm:
                        binding.viewPager2.setCurrentItem(2, false);
                        break;
                    case R.id.action_setting:
                        binding.viewPager2.setCurrentItem(3, false);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        binding.tvCountdownTimer.setVisibility(View.VISIBLE);
        setCountDownTimer();
        super.onResume();
    }

    private void setCountDownTimer() {
        if (countDownManager.isTimerRunning() && countDownManager.getTimeRemaining() > 0) {
            countDownManager = CountDownManager.initInstance(binding.tvCountdownTimer, () -> {
                sendActionToService(AudioService.ACTION_CLOSE);
                binding.tvCountdownTimer.setVisibility(View.GONE);
            });
            countDownManager.startTimer();
        } else if (countDownManager.getTimeRemaining() == 0) {
            binding.tvCountdownTimer.setVisibility(View.GONE);
        } else {
            binding.tvCountdownTimer.setText(countDownManager.millisToTimeFormat(countDownManager.getTimeRemaining()));
        }
    }

    private void pauseCountDownTimer() {
        if (countDownManager != null)
            countDownManager.pauseTimer();
    }

    private void resumeCountDownTimer() {
        if (!countDownManager.isTimerRunning() && countDownManager.getTimeRemaining() > 0) {
            if (countDownManager != null)
                countDownManager.resumeTimer();
        }
    }

    private void resetCountDownTimer() {
        if (countDownManager != null)
            countDownManager.resetTimer();
        binding.tvCountdownTimer.setVisibility(View.GONE);
    }

    private void setUpViewPager2() {
        ViewPager2MainAdapter pager2Adapter = new ViewPager2MainAdapter(this);
        binding.viewPager2.setAdapter(pager2Adapter);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle(R.string.action_home);
                        binding.bottomNav.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        getSupportActionBar().setTitle(R.string.action_sleep_calculator);
                        binding.bottomNav.getMenu().findItem(R.id.action_sleep_calculator).setChecked(true);
                        break;
                    case 2:
                        getSupportActionBar().setTitle(R.string.action_alarm);
                        binding.bottomNav.getMenu().findItem(R.id.action_alarm).setChecked(true);
                        break;
                    case 3:
                        getSupportActionBar().setTitle(R.string.action_setting);
                        binding.bottomNav.getMenu().findItem(R.id.action_setting).setChecked(true);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            if (binding.bottomNav.getSelectedItemId() != R.id.action_home) {
                binding.bottomNav.setSelectedItemId(R.id.action_home);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
                super.onBackPressed();
            }
        }
    }

    private void handleLayoutAudio(int actionAudio) {
        switch (actionAudio) {
            case AudioService.ACTION_START:
                handleInfoAudio();
                setStatusLayout();
                binding.layoutBottomAudio.setVisibility(View.VISIBLE);
                break;
            case AudioService.ACTION_PAUSE:
            case AudioService.ACTION_RESUME:
                setStatusLayout();
                break;
            case AudioService.ACTION_CLOSE:
                resetCountDownTimer();
                binding.layoutBottomAudio.setVisibility(View.GONE);
                break;
        }
    }

    private void handleInfoAudio() {
        if (mAudio != null) {
            Glide.with(this).load(mAudio.getImageResource()).into(binding.imgAudio);
            binding.tvTitleAudio.setText(mAudio.getNameAudio());
        }
        binding.layoutBottomAudio.setOnClickListener(v -> openAudioActivity());
        binding.imgPlayOrPause.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(AudioService.ACTION_PAUSE);
                actionAudio = AudioService.ACTION_PAUSE;
            } else {
                sendActionToService(AudioService.ACTION_RESUME);
                actionAudio = AudioService.ACTION_RESUME;
            }
        });
        binding.imgClose.setOnClickListener(v -> {
            resetCountDownTimer();
            sendActionToService(AudioService.ACTION_CLOSE);
        });
    }

    private void openAudioActivity() {
        Intent intent = new Intent(this, AudioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_audio", mAudio);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_audio", actionAudio);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setStatusLayout() {
        if (isPlaying) {
            resumeCountDownTimer();
            binding.imgPlayOrPause.setImageResource(R.drawable.pause_24dp);
        } else {
            pauseCountDownTimer();
            binding.imgPlayOrPause.setImageResource(R.drawable.play_24dp);
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra("action_audio_service", action);
        this.startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownManager.resetTimer();
        binding.tvCountdownTimer.setVisibility(View.GONE);
        stopService(new Intent(this, AudioService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}