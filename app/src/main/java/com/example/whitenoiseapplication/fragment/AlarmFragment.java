package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.activity.SetAlarmActivity;
import com.example.whitenoiseapplication.adapter.AlarmAdapter;
import com.example.whitenoiseapplication.callback.RecyclerViewItemAlarmTouchHelper;
import com.example.whitenoiseapplication.listener.IClickItemAlarm;
import com.example.whitenoiseapplication.listener.ItemTouchHelperListener;
import com.example.whitenoiseapplication.listener.OnSwitchCompatListener;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.util.DateTimeInterval;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;
import com.example.whitenoiseapplication.viewmodel.CreateAlarmViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AlarmFragment extends Fragment implements ItemTouchHelperListener, OnSwitchCompatListener {
    private AlarmsListViewModel alarmsListViewModel;
    private CreateAlarmViewModel createAlarmViewModel;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingButton;
    private RelativeLayout layout;
    private AlarmAdapter mAlarmAdapter;
    private List<Alarm> mAlarmList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAlarmViewModel = new ViewModelProvider(this).get(CreateAlarmViewModel.class);
        alarmsListViewModel = new ViewModelProvider(this).get(AlarmsListViewModel.class);
        alarmsListViewModel.getListLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if (alarms != null) {
                    mAlarmList = alarms;
                    mAlarmAdapter.setListAlarm(alarms);
                }
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.rcv_alarm);
        mFloatingButton = view.findViewById(R.id.btn_add_alarm);
        layout = view.findViewById(R.id.layout_alarm_fragment);

        mAlarmAdapter = new AlarmAdapter(getContext(), createAlarmViewModel, alarmsListViewModel, mAlarmList, (IClickItemAlarm) alarm -> {
            Intent intent = new Intent(getActivity(), SetAlarmActivity.class);
            intent.putExtra("title_alarm_activity", getString(R.string.fix_alarm));
            intent.putExtra("adjust_alarm_setting", true);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object_alarm", alarm);
            intent.putExtra("bundle_alarm", bundle);
            startActivity(intent);
        }, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAlarmAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFloatingButton.show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && mFloatingButton.isShown()) {
                    mFloatingButton.hide();
                }
            }
        });
        mFloatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetAlarmActivity.class);
            intent.putExtra("title_alarm_activity", getString(R.string.add_alarm));
            startActivity(intent);
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemAlarmTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerView);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AlarmAdapter.AlarmViewHolder) {
            Alarm alarm = mAlarmList.get(viewHolder.getBindingAdapterPosition());
            int deletedIndex = viewHolder.getBindingAdapterPosition();
            mAlarmAdapter.removeItem(alarm, deletedIndex);

            Snackbar snackbar = Snackbar.make(layout, R.string.mesage_delete_alarm, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.navy));
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setAction(R.string.undo, v -> {
                mAlarmAdapter.undoItem(alarm, deletedIndex);
                if (deletedIndex == 0 || deletedIndex == mAlarmList.size() - 1)
                    mRecyclerView.scrollToPosition(deletedIndex);
            });
            snackbar.show();
        }
    }

    @Override
    public void onClickSwitchCompat(Alarm alarm) {
        if (alarm.isAlarmEnabled()) {
            alarm.cancelAlarm(getContext());
            alarmsListViewModel.update(alarm);
        } else {
            alarm.schedule(getContext());
            alarmsListViewModel.update(alarm);
            Toast.makeText(getContext(), DateTimeInterval.getDateTimeInterval(alarm, getContext()), Toast.LENGTH_LONG).show();
        }
    }
}
