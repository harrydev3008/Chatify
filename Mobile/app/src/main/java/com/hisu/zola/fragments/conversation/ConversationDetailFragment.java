package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hisu.zola.MainActivity;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentConversationDetailBinding;

public class ConversationDetailFragment extends Fragment {

    public static final String USER_ARGS = "USER_DETAIL";
    private FragmentConversationDetailBinding mBinding;
    private MainActivity mainActivity;
    private User user;

    public static ConversationDetailFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(USER_ARGS, user);
        ConversationDetailFragment fragment = new ConversationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserDetail(user);
        addActionForBackBtn();
        addActionForEventChangeNickName();
        addActionForEventViewSentFiles();
        addActionForEventDeleteConversation();
        addActionForEventUnfriend();
    }

    private void loadUserDetail(User user) {
        mBinding.tvFriendName.setText(user.getUsername());
        Glide.with(mainActivity).load(user.getAvatarURL()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mBinding.imvFriendPfp);
    }

    private void addActionForBackBtn() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForEventChangeNickName() {
        mBinding.tvChangeNickName.setOnClickListener(view -> {
            ChangeNickNameBottomSheetFragment bottomSheetFragment = new ChangeNickNameBottomSheetFragment();
            bottomSheetFragment.setButtonClickListener(bottomSheetFragment::dismiss);
            bottomSheetFragment.show(mainActivity.getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void addActionForEventViewSentFiles() {
        //Todo: allow user view all files sent
        mBinding.tvSentFile.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "ViewSentFiles", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForEventDeleteConversation() {
        //Todo: allow user remove conversation
        mBinding.tvDeleteConversation.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "DeleteConversation", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForEventUnfriend() {
        //Todo: allow user unfriend
        mBinding.tvUnfriend.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "Unfriend", Toast.LENGTH_SHORT).show();
        });
    }
}