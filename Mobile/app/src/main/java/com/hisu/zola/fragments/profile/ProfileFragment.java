package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentProfileBinding;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserInfo();
        addActionForBtnEditProfile();
        addActionForBtnSetting();
        mMainActivity.setProgressbarVisibility(View.GONE);
    }

    private void loadUserInfo() {
        User user = LocalDataManager.getCurrentUserInfo();

        if(user.getAvatarURL() == null || user.getAvatarURL().isEmpty())
            mBinding.cimvUserAvatar.setImageBitmap(ImageConvertUtil.createImageFromText(mMainActivity, 150, 150, user.getUsername()));
        else
            Glide.with(mMainActivity).load(user.getAvatarURL())
                    .placeholder(R.drawable.bg_profile).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(mBinding.cimvUserAvatar);

        mBinding.tvGender.setText(user.isGender() ? getString(R.string.gender_m) : getString(R.string.gender_f));
        mBinding.tvDob.setText(user.getDob());
        mBinding.tvDisplayName.setText(user.getUsername());
        mBinding.tvPhoneNumber.setText(user.getPhoneNumber());
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