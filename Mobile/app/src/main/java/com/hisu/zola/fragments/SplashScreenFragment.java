package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding binding;
    private MainActivity mainActivity;

    public static final long DELAY_TIME = 2 * 1000; //2 secs

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if(isUserLoggedIn())
                mainActivity.setFragment(new HomeFragment());
            else
                mainActivity.setFragment(new StartScreenFragment());

        }, DELAY_TIME);

        return binding.getRoot();
    }

//  Todo: Write method to check user login/logout state
    private boolean isUserLoggedIn() {
        return false;
    }
}