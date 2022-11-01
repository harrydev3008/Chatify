package com.hisu.zola.fragments.greet_new_user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentOnBoardingGetStartBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addActionForBtnStart();
    }

    private void addActionForBtnStart() {
        mBinding.btnStart.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.addFragmentToBackStack(new ConversationListFragment());
        });
    }

    @Override
    public void onDestroyView() {
        mBinding = null;
        super.onDestroyView();
    }
}