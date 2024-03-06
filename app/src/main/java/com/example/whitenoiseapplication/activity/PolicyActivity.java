package com.example.whitenoiseapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.whitenoiseapplication.R;

public class PolicyActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/policy.html");
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);

        getSupportActionBar().setTitle(R.string.privacy_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return true;
    }
}