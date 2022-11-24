package com.hisu.zola.fragments.greet_new_user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.OnBoardingViewPagerAdapter;
import com.hisu.zola.databinding.FragmentWelcomeOnBoardingBinding;

public class WelcomeOnBoardingFragment extends Fragment {

    private FragmentWelcomeOnBoardingBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentWelcomeOnBoardingBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
        mainActivity.clearDB();
        addActionForBtnSkip();
        addActionForBtnNext();
    }

    private void initViewPager() {
        mBinding.vpOnBoarding.setAdapter(new OnBoardingViewPagerAdapter(mainActivity));
        mBinding.welcomeIndicator.setViewPager(mBinding.vpOnBoarding);

        mBinding.vpOnBoarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    mBinding.tvSkip.setVisibility(View.GONE);
                    mBinding.rlContainer.setVisibility(View.GONE);
                } else {
                    mBinding.tvSkip.setVisibility(View.VISIBLE);
                    mBinding.rlContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addActionForBtnSkip() {
        mBinding.tvSkip.setOnClickListener(view -> mBinding.vpOnBoarding.setCurrentItem(3));
    }

    private void addActionForBtnNext() {
        mBinding.tvNext.setOnClickListener(view -> {
            int currentItem = mBinding.vpOnBoarding.getCurrentItem();
            if (currentItem < 3)
                mBinding.vpOnBoarding.setCurrentItem(currentItem + 1);
        });
    }

    @Override
    public void onDestroyView() {
        mBinding = null;
        super.onDestroyView();
    }
}