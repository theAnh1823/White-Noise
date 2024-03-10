package com.example.whitenoiseapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.BlockedListAdapter;
import com.example.whitenoiseapplication.listener.IClickItemAudioListener;
import com.example.whitenoiseapplication.model.Audio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlockedListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BlockedListAdapter blockedListAdapter;
    private List<Audio> mListBlocked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_list);

        getSupportActionBar().setTitle(R.string.blocked_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rcv_blocked_list);
        mListBlocked = getBlockedList();
        blockedListAdapter = new BlockedListAdapter(this, mListBlocked, new IClickItemAudioListener() {
            @Override
            public void onClickItemAudio(Audio audio) {
                unBlockItem(audio);
            }

            @Override
            public void onClickMore(Audio audio) {

            }

            @Override
            public void onClickFavorite(Audio audio) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(blockedListAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void unBlockItem(Audio audio) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("list_audios");
        audio.setBlocked(false);
        mDatabase.child("" + audio.getId()).updateChildren(audio.toMap());
        blockedListAdapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.noti_toast_unblock), Toast.LENGTH_SHORT).show();
    }

    private List<Audio> getBlockedList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_audios");

        List<Audio> list = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListBlocked != null) {
                    list.clear();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Audio audio = postSnapshot.getValue(Audio.class);
//                    Log.e("TAG", audio.toString());
                    if (audio != null && audio.isBlocked()) {
                        list.add(audio);
                    }
                }
                blockedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Get list audio failed!", Toast.LENGTH_SHORT).show();
            }
        });
        return list;
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
