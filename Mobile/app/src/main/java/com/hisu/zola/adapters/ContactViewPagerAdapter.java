package com.hisu.zola.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hisu.zola.fragments.contact.ContactFriendFragment;
import com.hisu.zola.fragments.contact.ContactGroupFragment;

public class ContactViewPagerAdapter extends FragmentStateAdapter {

    public ContactViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0)
            return new ContactFriendFragment();
        return new ContactGroupFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}