package com.example.whitenoiseapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whitenoiseapplication.R;
import com.example.whitenoiseapplication.adapter.HomePager2Adapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private HomePager2Adapter pager2Adapter;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.home_fragment, container, false);
        initView();

        pager2Adapter = new HomePager2Adapter(this);
        mViewPager2.setAdapter(pager2Adapter);

        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
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
        return mView;
    }

    private void initView() {
        mTabLayout = mView.findViewById(R.id.tab_layout);
        mViewPager2 = mView.findViewById(R.id.home_viewpager2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
