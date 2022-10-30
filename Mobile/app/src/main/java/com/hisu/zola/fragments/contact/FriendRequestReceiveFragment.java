package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.FriendRequestReceiveAdapter;
import com.hisu.zola.adapters.FriendRequestSendAdapter;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentFriendRequestReceiveBinding;
import com.hisu.zola.databinding.FragmentFriendRequestSendBinding;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class FriendRequestReceiveFragment extends Fragment {

    private FragmentFriendRequestReceiveBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendRequestReceiveBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User curUser = LocalDataManager.getCurrentUserInfo();

        mBinding.rvFriendRequestReceive.setAdapter(
                new FriendRequestReceiveAdapter(curUser.getFriends(), mainActivity)
        );

        mBinding.rvFriendRequestReceive.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        getReceiveRequests();
    }

    private void getReceiveRequests() {
        if(mBinding.rvFriendRequestReceive.getAdapter().getItemCount() != 0)
            mBinding.imvNoRequestReceive.setVisibility(View.GONE);
        else
            mBinding.imvNoRequestReceive.setVisibility(View.VISIBLE);
    }
}