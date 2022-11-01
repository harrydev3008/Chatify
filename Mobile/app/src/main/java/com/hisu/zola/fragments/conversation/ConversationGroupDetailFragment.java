package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentConversationGroupDetailBinding;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

public class ConversationGroupDetailFragment extends Fragment {

    public static final String GROUP_ARGS = "GROUP_DETAIL";
    private FragmentConversationGroupDetailBinding mBinding;
    private MainActivity mainActivity;
    private Conversation conversation;
    private User currentUser;

    public static ConversationGroupDetailFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(GROUP_ARGS, conversation);
        ConversationGroupDetailFragment fragment = new ConversationGroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        currentUser = LocalDataManager.getCurrentUserInfo();

        if (getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(GROUP_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationGroupDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadConversationInfo();
        backToPrevPage();
        addActionForBtnViewMember();
    }

    private void loadConversationInfo() {
        mBinding.imvGroupPfp.setImageBitmap(ImageConvertUtil.createImageFromText(mainActivity, 150,150, conversation.getLabel()));
        mBinding.tvGroupName.setText(conversation.getLabel());
        String memberAmount = mBinding.tvMembers.getText() + " (" + conversation.getMember().size() + ")";
        mBinding.tvMembers.setText(memberAmount);

        if(conversation.getCreatedBy().getId().equalsIgnoreCase(currentUser.getId())) {
            mBinding.tvAddMembers.setVisibility(View.VISIBLE);
            mBinding.tvDisbandGroup.setVisibility(View.VISIBLE);
            mBinding.tvChangeAdmin.setVisibility(View.VISIBLE);
            addActionForBtnAddMember();
        }
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnAddMember() {
        mBinding.tvAddMembers.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new AddMemberToGroupFragment());
        });
    }

    private void addActionForBtnViewMember() {
        mBinding.tvMembers.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(ViewGroupMemberFragment.newInstance(conversation));
        });
    }
}