package com.example.whitenoiseapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation zoomAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        binding.imgLogo.startAnimation(zoomAnimation);

        new Handler().postDelayed((Runnable) () -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1500);
    }
}
