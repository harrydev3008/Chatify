package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestReceiveAdapter;
import com.hisu.zola.adapters.FriendRequestSendAdapter;
import com.hisu.zola.databinding.FragmentFriendRequestReceiveBinding;
import com.hisu.zola.databinding.FragmentFriendRequestSendBinding;

import java.util.List;

public class FriendRequestReceiveFragment extends Fragment {

    private FragmentFriendRequestReceiveBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentFriendRequestReceiveBinding.inflate(inflater, container, false);


        mBinding.rvFriendRequestReceive.setAdapter(
                new FriendRequestReceiveAdapter(List.of("Harry Nguyen", "John Doe", "Marry Jane"), mainActivity)
        );

        mBinding.rvFriendRequestReceive.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        return mBinding.getRoot();
    }
}