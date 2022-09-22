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

    private FragmentStartScreenBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentStartScreenBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        mBinding.vpStarter.setAdapter(
                new StarterSliderAdapter(List.of(
                    new StarterSliderItem(
                            R.drawable.bg_chat,
                            "Nhắn tin dễ dàng",
                            "Nhắn tin với bạn bè, người thân nhanh chóng, dễ dàng!"),
                        new StarterSliderItem(
                                R.drawable.bg_audio_call,
                                "Trò chuyện thuận tiện",
                                "Trò chuyện thuận tiện mọi lúc mọi nơi một cách ổn định nhất!"),
                        new StarterSliderItem(
                                R.drawable.bg_video_call,
                                "Gọi video ổn định",
                                "Tán gẫu thật đã với chất lượng video ổn định mọi lúc, mọi nơi!")
                ))
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