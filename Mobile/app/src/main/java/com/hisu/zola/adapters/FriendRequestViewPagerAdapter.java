package com.hisu.zola.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hisu.zola.fragments.contact.FriendRequestReceiveFragment;
import com.hisu.zola.fragments.contact.FriendRequestSendFragment;

public class FriendRequestViewPagerAdapter extends FragmentStateAdapter {

    public FriendRequestViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0)
            return new FriendRequestReceiveFragment();
        return new FriendRequestSendFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}