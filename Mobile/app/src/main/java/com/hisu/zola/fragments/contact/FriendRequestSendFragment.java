package com.hisu.zola.fragments.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.FriendRequestSendAdapter;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentFriendRequestSendBinding;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.List;

public class FriendRequestSendFragment extends Fragment {

    private FragmentFriendRequestSendBinding mBinding;
    private MainActivity mainActivity;
    private UserRepository userRepository;
    private FriendRequestSendAdapter sendAdapter;

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

        userRepository = new UserRepository(mainActivity.getApplication());

        sendAdapter = new FriendRequestSendAdapter(mainActivity);

        mBinding.rvFriendRequestSend.setLayoutManager(
                new LinearLayoutManager(
                        mainActivity, LinearLayoutManager.VERTICAL, false
                )
        );

        User curUser = LocalDataManager.getCurrentUserInfo();
        userRepository.getUser(curUser.getId()).observe(mainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) return;

                List<User> userList = user.getSendRequestQueue();

                sendAdapter.setRequestList(user.getSendRequestQueue());
                mBinding.rvFriendRequestSend.setAdapter(sendAdapter);

                getSendRequests();
            }
        });
    }

    private void getSendRequests() {
        if (sendAdapter.getItemCount() != 0)
            mBinding.imvNoRequestSend.setVisibility(View.GONE);
        else
            mBinding.imvNoRequestSend.setVisibility(View.VISIBLE);
    }
}