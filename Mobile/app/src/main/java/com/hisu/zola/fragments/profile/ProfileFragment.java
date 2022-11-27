package com.hisu.zola.fragments.profile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentProfileBinding;
import com.hisu.zola.util.converter.ImageConvertUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private MainActivity mMainActivity;
    private UserRepository repository;

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
        repository = new UserRepository(mMainActivity.getApplication());
        loadUserInfo();
        addActionForBtnEditProfile();
        addActionForBtnSetting();
        mMainActivity.setProgressbarVisibility(View.GONE);
    }

    private void loadUserInfo() {
        User currentUser = LocalDataManager.getCurrentUserInfo();

        repository.getUser(currentUser.getId()).observe(mMainActivity, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) {
                    repository.insertOrUpdate(currentUser);
                    return;
                }
                Log.e("pfp", user.getAvatarURL());
                if (user.getAvatarURL() == null || user.getAvatarURL().isEmpty())
                    mBinding.cimvUserAvatar.setImageBitmap(ImageConvertUtil.createImageFromText(mMainActivity, 150, 150, user.getUsername()));
                else
                    Glide.with(mMainActivity).asBitmap().load(user.getAvatarURL())
                            .placeholder(AppCompatResources.getDrawable(mMainActivity, R.drawable.ic_img_place_holder))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mBinding.cimvUserAvatar.setImageBitmap(resource);
                                    mBinding.cimvUserAvatar.setVisibility(View.VISIBLE);
                                }
                            });

                mBinding.tvGender.setText(user.isGender() ? mMainActivity.getString(R.string.gender_m) : mMainActivity.getString(R.string.gender_f));
                Date date = Date.from(Instant.parse(user.getDob()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                mBinding.tvDob.setText(dateFormat.format(date));
                mBinding.tvDisplayName.setText(user.getUsername());
                mBinding.tvPhoneNumber.setText(user.getPhoneNumber());
            }
        });
    }

    private void addActionForBtnEditProfile() {
        mBinding.btnEditProfile.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .add(mMainActivity.getViewContainerID(), new EditProfileFragment())
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