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
import com.hisu.zola.entity.StarterSliderItem;

import java.util.List;

public class StartScreenFragment extends Fragment {

    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();

        FragmentStartScreenBinding mBinding = FragmentStartScreenBinding.inflate(
                inflater, container, false
        );

        mBinding.vpStarter.setAdapter(
                new StarterSliderAdapter(List.of(
                        new StarterSliderItem(
                                R.drawable.bg_chat,
                                getString(R.string.feature_chat),
                                getString(R.string.feature_chat_desc)
                        ),
                        new StarterSliderItem(
                                R.drawable.bg_audio_call,
                                getString(R.string.feature_audio_call),
                                getString(R.string.feature_audio_call_desc)
                        ),
                        new StarterSliderItem(
                                R.drawable.bg_video_call,
                                getString(R.string.feature_video_call),
                                getString(R.string.feature_video_call_desc)
                        )))
        );

        mBinding.circleIndicator.setViewPager(mBinding.vpStarter);

        mBinding.btnLogin.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(mMainActivity.getViewContainerID(), new RegisterFragment())
                    .commit();
        });

        mBinding.btnRegister.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(mMainActivity.getViewContainerID(), new RegisterFragment())
                    .commit();
        });

        return mBinding.getRoot();
    }
}