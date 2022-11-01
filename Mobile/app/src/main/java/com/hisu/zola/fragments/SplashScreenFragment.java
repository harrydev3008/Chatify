package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.databinding.FragmentSplashScreenBinding;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.local.LocalDataManager;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding mBinding;
    private MainActivity mMainActivity;

    public static final long DELAY_TIME = 2 * 1000; //2 secs

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            mMainActivity.setBottomNavVisibility(View.GONE);

            if (isUserLoggedIn()) {
                mMainActivity.setBottomNavVisibility(View.VISIBLE);
                mMainActivity.addFragmentToBackStack(new ConversationListFragment());
            } else
                mMainActivity.setFragment(new StartScreenFragment());

        }, DELAY_TIME);
    }

    private boolean isUserLoggedIn() {
        return LocalDataManager.getUserLoginState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}