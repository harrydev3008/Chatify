package com.hisu.zola.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.StarterSliderAdapter;
import com.hisu.zola.databinding.FragmentStartScreenBinding;
import com.hisu.zola.model.StarterSliderItem;
import com.hisu.zola.fragments.authenticate.LoginFragment;
import com.hisu.zola.fragments.authenticate.RegisterFragment;

import java.util.List;

public class StartScreenFragment extends Fragment {

    private FragmentStartScreenBinding mBinding;
    private MainActivity mMainActivity;

    private static final int DELAY_TIME = 3 * 1000; //3s

    private final Handler mSliderHandler = new Handler(Looper.getMainLooper());

    private final Runnable mSliderRunnable = () -> {
        int currentPosition = mBinding.vpStarter.getCurrentItem();

        if(currentPosition == 2) //Since there are only 3 items, so i hard-coded xD don't be like me
            mBinding.vpStarter.setCurrentItem(0);
        else
            mBinding.vpStarter.setCurrentItem(currentPosition + 1);
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();

        mBinding = FragmentStartScreenBinding.inflate(inflater, container, false);

        initViewPager();
        addActionForBtnLogin();
        addActionForBtnRegister();

        return mBinding.getRoot();
    }

    private void initViewPager() {
        mBinding.vpStarter.setAdapter(new StarterSliderAdapter(getSliderItems()));
        mBinding.circleIndicator.setViewPager(mBinding.vpStarter);
        mBinding.vpStarter.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mSliderHandler.removeCallbacks(mSliderRunnable);
                mSliderHandler.postDelayed(mSliderRunnable, DELAY_TIME);
            }
        });
    }

    private void addActionForBtnLogin() {
        mBinding.btnLogin.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mMainActivity.getViewContainerID(), new LoginFragment())
                    .commit();
        });
    }

    private void addActionForBtnRegister() {
        mBinding.btnRegister.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(mMainActivity.getViewContainerID(), new RegisterFragment())
                    .commit();
        });
    }

    private List<StarterSliderItem> getSliderItems() {
        return List.of(
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
                ));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSliderHandler.removeCallbacks(mSliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSliderHandler.postDelayed(mSliderRunnable, DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSliderHandler.removeCallbacks(mSliderRunnable);
    }
}