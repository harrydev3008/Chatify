package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ViewFriendAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentViewGroupMemberBinding;
import com.hisu.zola.listeners.IOnRemoveUserListener;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.Serializable;
import java.util.List;

public class ViewGroupMemberFragment extends Fragment {

    public static final String MEMBER_ARGS = "MEMBER_ARGS";
    private FragmentViewGroupMemberBinding mBinding;
    private MainActivity mainActivity;
    private Conversation conversation;

    public static ViewGroupMemberFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(MEMBER_ARGS, conversation);
        ViewGroupMemberFragment fragment = new ViewGroupMemberFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(MEMBER_ARGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentViewGroupMemberBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backToPrevPage();
        addActionForBtnAddMember();
        initRecyclerView();
    }

    private void initRecyclerView() {
        ViewFriendAdapter adapter = new ViewFriendAdapter(mainActivity);
        adapter.setAdmin(conversation.getCreatedBy());
        adapter.setMembers(conversation.getMember());

        String textPlaceHolder = mBinding.tvMemberQuan.getText().toString() +  " " + conversation.getMember().size() + ":";
        mBinding.tvMemberQuan.setText(textPlaceHolder);

        if(LocalDataManager.getCurrentUserInfo().getId().equalsIgnoreCase(conversation.getCreatedBy().getId())) {
            adapter.setOnRemoveUserListener(user -> Toast.makeText(mainActivity, user.getUsername() + " removed!", Toast.LENGTH_SHORT).show());
            adapter.setAdmin(true);
        }

        mBinding.rvMembers.setAdapter(adapter);
        mBinding.rvMembers.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnAddMember() {
        mBinding.iBtnAddMember.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new AddMemberToGroupFragment());
        });
    }
}