package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.StarterSliderAdapter;
import com.hisu.zola.databinding.FragmentStartScreenBinding;

import java.util.List;

public class StartScreenFragment extends Fragment {

    private FragmentStartScreenBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentStartScreenBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        mBinding.vpStarter.setAdapter(
                new StarterSliderAdapter(
                        List.of(R.drawable.audio_call,
                                R.drawable.chat,
                                R.drawable.video_call)
                )
        );

        mBinding.circleIndicator.setViewPager(mBinding.vpStarter);

        mBinding.btnLogin.setOnClickListener(view -> {
            mMainActivity.setFragment(new LoginFragment());
        });

        mBinding.btnRegister.setOnClickListener(view -> {
            mMainActivity.setFragment(new RegisterFragment());
        });

        return mBinding.getRoot();
    }
}