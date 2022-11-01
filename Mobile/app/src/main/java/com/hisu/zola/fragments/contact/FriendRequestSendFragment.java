package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentFriendRequestSendBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.rvFriendRequestSend.setAdapter(
                new FriendRequestSendAdapter(List.of(), mainActivity)
        );

        mBinding.rvFriendRequestSend.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        getSentRequests();
    }

    private void getSentRequests() {
        if(mBinding.rvFriendRequestSend.getAdapter().getItemCount() != 0)
            mBinding.imvNoRequestSend.setVisibility(View.GONE);
        else
            mBinding.imvNoRequestSend.setVisibility(View.VISIBLE);
    }
}