package com.example.whitenoiseapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whitenoiseapplication.R;

public class SplashActivity extends AppCompatActivity {
    Animation zoomAnimation;
    ImageView imgLogo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        zoomAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        imgLogo = findViewById(R.id.img_logo);
        imgLogo.startAnimation(zoomAnimation);

        new Handler().postDelayed((Runnable) () -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1500);
    }
}
