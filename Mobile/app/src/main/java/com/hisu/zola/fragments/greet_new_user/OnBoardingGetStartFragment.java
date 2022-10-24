package com.hisu.zola.fragments.greet_new_user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.databinding.FragmentOnBoardingGetStartBinding;
import com.hisu.zola.fragments.conversation.ConversationListFragment;

public class OnBoardingGetStartFragment extends Fragment {

   private FragmentOnBoardingGetStartBinding mBinding;
   private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentOnBoardingGetStartBinding.inflate(inflater, container, false);

        addActionForBtnStart();

        return mBinding.getRoot();
    }

    private void addActionForBtnStart() {
        mBinding.btnStart.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.addFragmentToBackStack(new ConversationListFragment());
        });
    }
}