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
import com.example.whitenoiseapplication.model.CountDownManager;
import com.example.whitenoiseapplication.model.TimeSingleton;
import com.example.whitenoiseapplication.service.AudioService;
import com.example.whitenoiseapplication.util.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private BottomNavigationView mNavigationView;
    private ViewPager2 mViewPager2;
    private RelativeLayout layoutBottom;
    private ImageView imageAudio, imagePlayOrPause, imageClose;
    private TextView tvTitleAudio;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvCountDown;
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
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        initView();
        countDownManager = CountDownManager.initInstance(tvCountDown, () -> {
            sendActionToService(AudioService.ACTION_CLOSE);
            tvCountDown.setVisibility(View.GONE);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        SharedPreferences sharedPreferences = getSharedPreferences("pref_switch_language", MODE_PRIVATE);
        boolean isVietnameseLanguage = sharedPreferences.getBoolean("value", false);
        if (isVietnameseLanguage) {
            context = LocaleHelper.setLocale(this, "vi");
        } else {
            context = LocaleHelper.setLocale(this, "en");
        }

        mViewPager2.setUserInputEnabled(false);
        setUpViewPager2();

        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_bottom_nav);
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
        tvCountDown = findViewById(R.id.tv_countdown_timer);
        tvCountDown.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timer, 0, 0, 0);
    }

    @Override
    protected void onResume() {
        tvCountDown.setVisibility(View.VISIBLE);
        setCountDownTimer();
        super.onResume();
    }

    private void setCountDownTimer() {
        if (countDownManager.isTimerRunning() && countDownManager.getTimeRemaining() > 0) {
            countDownManager = CountDownManager.initInstance(tvCountDown, () -> {
                sendActionToService(AudioService.ACTION_CLOSE);
                tvCountDown.setVisibility(View.GONE);
            });
            countDownManager.startTimer();
        } else if (countDownManager.getTimeRemaining() == 0) {
            tvCountDown.setVisibility(View.GONE);
        } else {
            tvCountDown.setText(countDownManager.millisToTimeFormat(countDownManager.getTimeRemaining()));
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
        tvCountDown.setVisibility(View.GONE);
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
        imageClose.setOnClickListener(v -> {
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
        countDownManager.resetTimer();
        tvCountDown.setVisibility(View.GONE);
        stopService(new Intent(this, AudioService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}