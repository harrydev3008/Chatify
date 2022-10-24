package com.hisu.zola.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hisu.zola.fragments.greet_new_user.OnBoardingFragmentOne;
import com.hisu.zola.fragments.greet_new_user.OnBoardingFragmentThree;
import com.hisu.zola.fragments.greet_new_user.OnBoardingFragmentTwo;
import com.hisu.zola.fragments.greet_new_user.OnBoardingGetStartFragment;

public class OnBoardingViewPagerAdapter extends FragmentStateAdapter {

    public OnBoardingViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new OnBoardingFragmentTwo();
            case 2: return new OnBoardingFragmentThree();
            case 3: return new OnBoardingGetStartFragment();
            default: return new OnBoardingFragmentOne();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}