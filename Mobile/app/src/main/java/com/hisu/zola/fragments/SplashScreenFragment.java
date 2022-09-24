package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.databinding.FragmentSplashScreenBinding;
import com.hisu.zola.util.local.LocalDataManager;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding mBinding;
    private MainActivity mMainActivity;

    public static final long DELAY_TIME = 2 * 1000; //2 secs

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();

        mBinding = FragmentSplashScreenBinding.inflate(inflater, container, false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if(isUserLoggedIn())
                mMainActivity.setFragment(new HomeFragment());
            else
                mMainActivity.setFragment(new StartScreenFragment());

        }, DELAY_TIME);

        return mBinding.getRoot();
    }

    private boolean isUserLoggedIn() {
        return LocalDataManager.getUserLoginState();
    }
}