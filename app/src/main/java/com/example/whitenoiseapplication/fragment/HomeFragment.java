package com.example.whitenoiseapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.HomePager2Adapter;
import com.example.whitenoiseapplication.databinding.HomeFragmentBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        HomeFragmentBinding binding = HomeFragmentBinding.inflate(inflater, container, false);
        showInternetStatusDialog();

        HomePager2Adapter pager2Adapter = new HomePager2Adapter(this);
        binding.homeViewPager2.setAdapter(pager2Adapter);

        new TabLayoutMediator(binding.tabLayout, binding.homeViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(getString(R.string.all));
                        break;
                    case 1:
                        tab.setText(getString(R.string.favorite));
                        break;
                }
            }
        }).attach();
        return binding.getRoot();
    }

    private void showInternetStatusDialog() {
        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(
                requireActivity(),
                getLifecycle()
        );

        DialogPropertiesPendulum properties = builder.getDialogProperties();

        properties.setCancelable(true);
        properties.setNoInternetConnectionTitle(getString(R.string.no_internet));
        properties.setNoInternetConnectionMessage(getString(R.string.no_internet_connection_message));
        properties.setShowInternetOnButtons(true);
        properties.setPleaseTurnOnText(getString(R.string.please_turn_on));
        properties.setWifiOnButtonText(getString(R.string.wifi));
        properties.setMobileDataOnButtonText(getString(R.string.mobile_data));

        properties.setOnAirplaneModeTitle(getString(R.string.no_internet));
        properties.setOnAirplaneModeMessage(getString(R.string.airplane_mode_message));
        properties.setPleaseTurnOffText(getString(R.string.please_turn_off));
        properties.setAirplaneModeOffButtonText(getString(R.string.airplane_mode));
        properties.setShowAirplaneModeOffButtons(true);

        builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
