package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();

        mBinding = FragmentProfileBinding.inflate(inflater, container, false);

        addActionForBtnEditProfile();
        addActionForBtnSetting();

        return mBinding.getRoot();
    }

    private void addActionForBtnEditProfile() {
        mBinding.btnEditProfile.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .add(mMainActivity.getViewContainerID(),new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void addActionForBtnSetting() {
        mBinding.iBtnSetting.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .add(mMainActivity.getViewContainerID(), new SettingFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}