package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.activity.AudioActivity;
import com.example.whitenoiseapplication.activity.MainActivity;
import com.example.whitenoiseapplication.listener.IClickItemAudioListener;
import com.example.whitenoiseapplication.adapter.AudioAdapter;
import com.example.whitenoiseapplication.listener.IClickItemBottemSheet;
import com.example.whitenoiseapplication.model.Audio;
import com.example.whitenoiseapplication.model.Setting;
import com.example.whitenoiseapplication.model.TimeSingleton;
import com.example.whitenoiseapplication.service.AudioService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllAudioFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private AudioAdapter mAudioAdapter;
    private GridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchView mSearchView;
    private BottomSheetAudio mBottomSheetAudio;
    private int mCurrentTypeDisplay = Audio.TYPE_GRID;
    private List<Audio> mListAudio;
    private Menu mMenu;
    private int mCurrentPosition;
    private int currentPlayingAudioId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.all_audios_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.rcv_home_all_sounds);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mListAudio = getListAudio();
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAudioAdapter = new AudioAdapter(getActivity(), mListAudio, new IClickItemAudioListener() {
            @Override
            public void onClickItemAudio(Audio audio) {
                if (audio.isBlocked()) {
                    showDialogUnBlocked(audio);
                } else {
                    if (currentPlayingAudioId != audio.getId())
                        resetCountDownTimer(audio);
                    stopService();
                    openAudioActivity(audio);
                    clickStartService(audio);
                }
            }

            @Override
            public void onClickMore(Audio audio) {
                openBottomSheet(audio);
            }

            @Override
            public void onClickFavorite(Audio audio) {
                clickUnFavoriteItem(audio);
            }
        });
        mRecyclerView.setAdapter(mAudioAdapter);

        return view;
    }

    private void openAudioActivity(Audio audio) {
        Intent intent = new Intent(getActivity(), AudioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_audio", audio);
        bundle.putBoolean("status_player", true);
        bundle.putInt("action_audio", AudioService.ACTION_START);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showDialogUnBlocked(@NonNull Audio audio) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_dialog_blocked);
        dialog.setCancelable(false);
        Button btnUnBlocked = dialog.findViewById(R.id.btn_dialog_unblocked);
        Button btnCancel = dialog.findViewById(R.id.btn_bottom_sheet_cancel);
        ImageView imgAudio = dialog.findViewById(R.id.img_audio_dialog);
        TextView tvNameAudio = dialog.findViewById(R.id.tv_name_audio);
        tvNameAudio.setText(audio.getNameAudio());
        Glide.with(getActivity()).load(audio.getImageResource()).into(imgAudio);

        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnUnBlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBlockItem(audio);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openBottomSheet(@NonNull Audio audio) {
        String blockContent = audio.isBlocked() ? getString(R.string.unblock) : getString(R.string.block);
        String favoriteContent = audio.isFavorite() ? getString(R.string.unfavorite) : getString(R.string.favorite);
        int imageFavResource = audio.isFavorite() ? R.drawable.favorite_30dp : R.drawable.un_favorite;
        List<Setting> menuLists = new ArrayList<>();
        menuLists.add(new Setting(1, imageFavResource, favoriteContent));
        menuLists.add(new Setting(1, R.drawable.block_black, blockContent));
        mBottomSheetAudio = new BottomSheetAudio(audio, menuLists, new IClickItemBottemSheet() {
            @Override
            public void onItemClick(Setting setting) {
                if (setting.getNameItem().equals(getString(R.string.favorite)))
                    clickFavoriteItem(audio);
                else if (setting.getNameItem().equals(getString(R.string.unfavorite)))
                    clickUnFavoriteItem(audio);
                else if (setting.getNameItem().equals(getString(R.string.block)))
                    showDialogConfirmBlock(audio);
                else if (setting.getNameItem().equals(getString(R.string.unblock)))
                    unBlockItem(audio);
                menuLists.clear();
                mBottomSheetAudio.dismiss();
            }
        });
        mBottomSheetAudio.show(getActivity().getSupportFragmentManager(), mBottomSheetAudio.getTag());
    }

    private void showDialogConfirmBlock(Audio audio) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_dialog_blocked);
        TextView titleDialog = dialog.findViewById(R.id.tv_title_blocked);
        titleDialog.setText(R.string.confirm_block);
        Button btnUnBlocked = dialog.findViewById(R.id.btn_dialog_unblocked);
        btnUnBlocked.setText(R.string.block);
        Button btnCancel = dialog.findViewById(R.id.btn_bottom_sheet_cancel);
        ImageView imgAudio = dialog.findViewById(R.id.img_audio_dialog);
        TextView tvNameAudio = dialog.findViewById(R.id.tv_name_audio);
        tvNameAudio.setText(audio.getNameAudio());
        Glide.with(getActivity()).load(audio.getImageResource()).into(imgAudio);

        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnUnBlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockItem(audio);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void blockItem(@NonNull Audio audio) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("list_audios");
        audio.setBlocked(true);
        mDatabase.child("" + audio.getId()).updateChildren(audio.toMap());
        mAudioAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getString(R.string.noti_toast_block), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void unBlockItem(Audio audio) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("list_audios");
        audio.setBlocked(false);
        mDatabase.child("" + audio.getId()).updateChildren(audio.toMap());
        mAudioAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getString(R.string.noti_toast_unblock), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clickFavoriteItem(Audio audio) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("list_audios");
        audio.setFavorite(true);
        mDatabase.child("" + audio.getId()).updateChildren(audio.toMap());
        mAudioAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getString(R.string.noti_toast_favorite), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clickUnFavoriteItem(Audio audio) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("list_audios");
        audio.setFavorite(false);
        mDatabase.child("" + audio.getId()).updateChildren(audio.toMap());
        mAudioAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getString(R.string.noti_toast_unfavortite), Toast.LENGTH_SHORT).show();
    }

    private void clickStartService(Audio audio) {
        Intent intent = new Intent(getActivity(), AudioService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_audio", audio);
        intent.putExtras(bundle);

        getActivity().startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(getActivity(), AudioService.class);
        getActivity().stopService(intent);
    }

    private void resetCountDownTimer(Audio audio) {
        currentPlayingAudioId = audio.getId();
        if (MainActivity.countDownTimer != null) {
            MainActivity.countDownTimer.cancel();
            TimeSingleton.getInstance().setTimeRunning(false);
            TimeSingleton.getInstance().setTimeRemaining(0);
            MainActivity.countDownTimer = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_appbar_home, menu);
        this.mMenu = menu;
        setIconMenu();
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAudioAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAudioAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_layout) {
            setCurrentPosition();
            onClickChangeTypeDisplay();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTypeDisplayRecycleView(int typeDisplay) {
        if (mListAudio == null || mListAudio.isEmpty()) {
            return;
        }

        mCurrentTypeDisplay = typeDisplay;

        for (Audio audio : mListAudio) {
            audio.setTypeDisplay(typeDisplay);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClickChangeTypeDisplay() {
        if (mRecyclerView != null) {
            if (mCurrentTypeDisplay == Audio.TYPE_GRID) {
                setTypeDisplayRecycleView(Audio.TYPE_LIST);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
            } else if (mCurrentTypeDisplay == Audio.TYPE_LIST) {
                setTypeDisplayRecycleView(Audio.TYPE_GRID);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
            }
            mAudioAdapter.notifyDataSetChanged();
            setIconMenu();
            mRecyclerView.scrollToPosition(mCurrentPosition);
        }
    }

    public void setIconMenu() {
        switch (mCurrentTypeDisplay) {
            case Audio.TYPE_GRID:
                mMenu.getItem(1).setIcon(R.drawable.list_view);
                break;
            case Audio.TYPE_LIST:
                mMenu.getItem(1).setIcon(R.drawable.grid_view);
                break;
        }
    }

    private void setCurrentPosition() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        switch (mCurrentTypeDisplay) {
            case Audio.TYPE_GRID:
                mCurrentPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case Audio.TYPE_LIST:
                mCurrentPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
        }
    }

    @NonNull
    private List<Audio> getListAudio() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_audios");

        List<Audio> mList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListAudio != null) {
                    mList.clear();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Audio audio = postSnapshot.getValue(Audio.class);
                    if (audio != null) {
                        mList.add(audio);
                    }
                }
                mAudioAdapter.notifyDataSetChanged();
                setTypeDisplayRecycleView(mCurrentTypeDisplay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Get list audio failed!", Toast.LENGTH_SHORT).show();
            }
        });
        return mList;
    }
}
