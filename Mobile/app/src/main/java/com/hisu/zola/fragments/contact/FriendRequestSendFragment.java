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
import com.hisu.zola.adapters.FriendRequestSendAdapter;
import com.hisu.zola.databinding.FragmentFriendRequestSendBinding;

import java.util.List;

public class FriendRequestSendFragment extends Fragment {

    private FragmentFriendRequestSendBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentFriendRequestSendBinding.inflate(inflater, container, false);

        mBinding.rvFriendRequestSend.setAdapter(
                new FriendRequestSendAdapter(List.of("Harry Nguyen", "John Doe", "Marry Jane"), mainActivity)
        );

        mBinding.rvFriendRequestSend.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        return mBinding.getRoot();
    }
}