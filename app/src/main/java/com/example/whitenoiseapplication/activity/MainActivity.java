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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.ViewPager2MainAdapter;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.TimeSingleton;
import com.example.whitenoiseapplication.service.AudioService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mNavigationView;
    private ViewPager2 mViewPager2;
    private RelativeLayout layoutBottom;
    private ImageView imageAudio, imagePlayOrPause, imageClose;
    private TextView tvTitleAudio, tvCountdownTimer;
    public static CountDownTimer countDownTimer;
    private TimeSingleton timeSingleton;
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
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        timeSingleton = TimeSingleton.getInstance();
        initView();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        mViewPager2.setUserInputEnabled(false);
        setUpViewPager2();
        mNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint({"ResourceType", "NonConstantResourceId"})
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        mViewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.action_sleep_calculator:
                        mViewPager2.setCurrentItem(1, false);
                        break;
                    case R.id.action_alarm:
                        mViewPager2.setCurrentItem(2, false);
                        break;
                    case R.id.action_setting:
                        mViewPager2.setCurrentItem(3, false);
                        break;
                }
                return true;
            }
        });
    }

    private void initView() {
        mNavigationView = findViewById(R.id.bottom_nav);
        mViewPager2 = findViewById(R.id.view_pager2);

        layoutBottom = findViewById(R.id.layout_bottom_audio);
        imageAudio = findViewById(R.id.img_audio);
        imagePlayOrPause = findViewById(R.id.img_play_or_pause);
        imageClose = findViewById(R.id.img_close);
        tvTitleAudio = findViewById(R.id.tv_title_audio);
        tvCountdownTimer = findViewById(R.id.tv_countdown_timer);
        tvCountdownTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timer, 0, 0, 0);
    }

    @Override
    protected void onResume() {
        tvCountdownTimer.setVisibility(View.VISIBLE);
        if (timeSingleton.isTimeRunning() && timeSingleton.getTimeRemaining() >0) {
            startCountDownTimer(timeSingleton.getTimeRemaining());
        } else if (timeSingleton.getTimeRemaining() == 0) {
            tvCountdownTimer.setVisibility(View.GONE);
        } else {
            tvCountdownTimer.setText(millisToTimeFormat(timeSingleton.getTimeRemaining()));
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        super.onPause();
    }

    private void startCountDownTimer(long timeRemaining) {
        timeSingleton.setTimeRunning(true);
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeSingleton.setTimeRemaining(millisUntilFinished);
                tvCountdownTimer.setText(millisToTimeFormat(millisUntilFinished));
                Log.e("M", "MAIN ACTIVITY: " + millisToTimeFormat(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                sendActionToService(AudioService.ACTION_CLOSE);
                tvCountdownTimer.setVisibility(View.GONE);
            }
        }.start();
    }

    @SuppressLint("DefaultLocale")
    private String millisToTimeFormat(long millisUntilFinished) {
        long hour = (millisUntilFinished / 3600000) % 24;
        long min = (millisUntilFinished / 60000) % 60;
        long sec = (millisUntilFinished / 1000) % 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
        return String.format("%02d:%02d", min, sec);
    }

    private void pauseCountDownTimer() {
        timeSingleton.setTimeRunning(false);
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    private void resumeCountDownTimer() {
        timeSingleton.setTimeRunning(true);
        if (timeSingleton.isTimeRunning() && timeSingleton.getTimeRemaining() > 0)
            startCountDownTimer(timeSingleton.getTimeRemaining());
    }

    private void resetCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        tvCountdownTimer.setVisibility(View.GONE);
        timeSingleton.setTimeRunning(false);
        timeSingleton.setTimeRemaining(0);
    }

    private void setUpViewPager2() {
        ViewPager2MainAdapter pager2Adapter = new ViewPager2MainAdapter(this);
        mViewPager2.setAdapter(pager2Adapter);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle(R.string.action_home);
                        mNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        getSupportActionBar().setTitle(R.string.action_sleep_calculator);
                        mNavigationView.getMenu().findItem(R.id.action_sleep_calculator).setChecked(true);
                        break;
                    case 2:
                        getSupportActionBar().setTitle(R.string.action_alarm);
                        mNavigationView.getMenu().findItem(R.id.action_alarm).setChecked(true);
                        break;
                    case 3:
                        getSupportActionBar().setTitle(R.string.action_setting);
                        mNavigationView.getMenu().findItem(R.id.action_setting).setChecked(true);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            if (mNavigationView.getSelectedItemId() != R.id.action_home) {
                mNavigationView.setSelectedItemId(R.id.action_home);
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
                layoutBottom.setVisibility(View.VISIBLE);
                break;
            case AudioService.ACTION_PAUSE:
            case AudioService.ACTION_RESUME:
                setStatusLayout();
                break;
            case AudioService.ACTION_CLOSE:
                resetCountDownTimer();
                layoutBottom.setVisibility(View.GONE);
                break;
        }
    }

    private void handleInfoAudio() {
        if (mAudio != null) {
            Glide.with(this).load(mAudio.getImageResource()).into(imageAudio);
            tvTitleAudio.setText(mAudio.getNameAudio());
        }
        layoutBottom.setOnClickListener(v -> openAudioActivity());
        imagePlayOrPause.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(AudioService.ACTION_PAUSE);
                actionAudio = AudioService.ACTION_PAUSE;
            } else {
                sendActionToService(AudioService.ACTION_RESUME);
                actionAudio = AudioService.ACTION_RESUME;
            }
        });
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCountDownTimer();
                sendActionToService(AudioService.ACTION_CLOSE);
            }
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
            imagePlayOrPause.setImageResource(R.drawable.pause_24dp);
        } else {
            pauseCountDownTimer();
            imagePlayOrPause.setImageResource(R.drawable.play_24dp);
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
        pauseCountDownTimer();
        countDownTimer = null;
        timeSingleton.setTimeRemaining(0);
        tvCountdownTimer.setVisibility(View.GONE);
        stopService(new Intent(this, AudioService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}