package com.example.whitenoiseapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.whitenoiseapplication.databinding.AlarmFragmentBinding;
import com.example.whitenoiseapplication.listener.ItemTouchHelperListener;
import com.example.whitenoiseapplication.listener.OnSwitchCompatListener;
import com.example.whitenoiseapplication.model.Alarm;
import com.example.whitenoiseapplication.util.DateTimeInterval;
import com.example.whitenoiseapplication.viewmodel.AlarmsListViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AlarmFragment extends Fragment implements ItemTouchHelperListener, OnSwitchCompatListener {
    private AlarmFragmentBinding binding;
    private AlarmsListViewModel alarmsListViewModel;
    private AlarmAdapter mAlarmAdapter;
    private List<Alarm> mAlarmList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AlarmFragmentBinding.inflate(inflater, container, false);

        mAlarmAdapter = new AlarmAdapter(getContext(), alarmsListViewModel, mAlarmList, alarm -> {
            Intent intent = new Intent(getActivity(), SetAlarmActivity.class);
            intent.putExtra("title_alarm_activity", getString(R.string.fix_alarm));
            intent.putExtra("adjust_alarm_setting", true);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object_alarm", alarm);
            intent.putExtra("bundle_alarm", bundle);
            startActivity(intent);
        }, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(mAlarmAdapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.btnAddAlarm.show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && binding.btnAddAlarm.isShown()) {
                    binding.btnAddAlarm.hide();
                }
            }
        });
        binding.btnAddAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetAlarmActivity.class);
            intent.putExtra("title_alarm_activity", getString(R.string.add_alarm));
            startActivity(intent);
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemAlarmTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerView);
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AlarmAdapter.AlarmViewHolder) {
            Alarm alarm = mAlarmList.get(viewHolder.getBindingAdapterPosition());
            int deletedIndex = viewHolder.getBindingAdapterPosition();
            mAlarmAdapter.removeItem(alarm, deletedIndex);

            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.message_delete_alarm, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy));
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setAction(R.string.undo, v -> {
                mAlarmAdapter.undoItem(alarm, deletedIndex);
                if (deletedIndex == 0 || deletedIndex == mAlarmList.size() - 1)
                    binding.recyclerView.scrollToPosition(deletedIndex);
            });
            snackbar.show();
        }
    }

    @Override
    public void onClickSwitchCompat(@NonNull Alarm alarm) {
        if (alarm.isAlarmEnabled()) {
            alarm.cancelAlarm(requireContext());
        } else {
            alarm.schedule(requireContext());
            Toast.makeText(requireContext(), DateTimeInterval.getDateTimeInterval(alarm, requireContext()), Toast.LENGTH_LONG).show();
        }
        alarmsListViewModel.update(alarm);
    }
}
