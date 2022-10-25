package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hisu.zola.MainActivity;
import com.hisu.zola.databinding.FragmentConversationDetailBinding;

public class ConversationDetailFragment extends Fragment {

   private FragmentConversationDetailBinding mBinding;
   private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        mBinding = FragmentConversationDetailBinding.inflate(
                inflater, container, false
        );

        addActionForBackBtn();
        addActionForEventChangeNickName();
        addActionForEventViewSentFiles();
        addActionForEventDeleteConversation();
        addActionForEventUnfriend();

        return mBinding.getRoot();
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