package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestViewPagerAdapter;
import com.hisu.zola.databinding.FragmentFriendRequestBinding;
import com.hisu.zola.fragments.HomeFragment;

public class FriendRequestFragment extends Fragment {

    private FragmentFriendRequestBinding mBinding;
    private MainActivity mainActivity;
    private FriendRequestViewPagerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentFriendRequestBinding.inflate(inflater, container, false);

        addActionForBtnBackToPrev();
        initTabLayout();

        return mBinding.getRoot();
    }

    private void initTabLayout() {
        adapter = new FriendRequestViewPagerAdapter(mainActivity);
        mBinding.vpContainer.setAdapter(adapter);

        new TabLayoutMediator(mBinding.tlFriendRequest, mBinding.vpContainer, (tab, position) -> {
            switch (position) {
                case 0 : {
                    tab.setText(getString(R.string.friend_request_receive));
                    break;
                }

                case 1 : {
                    tab.setText(getString(R.string.friend_request_send));
                    break;
                }
            }
        }).attach();
    }

    private void addActionForBtnBackToPrev() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setFragment(HomeFragment.newInstance(HomeFragment.BACK_FROM_CONTACT_ARGS));
        });
    }
}