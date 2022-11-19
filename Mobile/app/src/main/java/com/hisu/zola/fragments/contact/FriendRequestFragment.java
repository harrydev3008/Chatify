package com.hisu.zola.fragments.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestViewPagerAdapter;
import com.hisu.zola.databinding.FragmentFriendRequestBinding;

public class FriendRequestFragment extends Fragment {

    private FragmentFriendRequestBinding mBinding;
    private MainActivity mainActivity;
    private FriendRequestViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendRequestBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addActionForBtnBackToPrev();
        initTabLayout();
    }

    private void initTabLayout() {
        adapter = new FriendRequestViewPagerAdapter(mainActivity);
        mBinding.vpContainer.setAdapter(adapter);

        new TabLayoutMediator(mBinding.tlFriendRequest, mBinding.vpContainer, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setText(mainActivity.getString(R.string.friend_request_receive));
                    break;
                }

                case 1: {
                    tab.setText(mainActivity.getString(R.string.friend_request_send));
                    break;
                }
            }
        }).attach();
    }

    private void addActionForBtnBackToPrev() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}