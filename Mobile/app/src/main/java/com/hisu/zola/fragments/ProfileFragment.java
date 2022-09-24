package com.hisu.zola.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

        addActionForBtnLogout();
        addActionForBtnEditProfile();
        addActionForBtnChangePassword();

        return mBinding.getRoot();
    }

    private void addActionForBtnLogout() {
        mBinding.btnLogOut.setOnClickListener(view -> {
            new AlertDialog.Builder(mMainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirm))
                    .setPositiveButton(getString(R.string.logout),
                            (dialogInterface, i) -> mMainActivity.logOut())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
    }

    private void addActionForBtnEditProfile() {
        mBinding.btnEditProfile.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mMainActivity.getViewContainerID(), new EditProfileFragment())
                    .commit();
        });
    }

    private void addActionForBtnChangePassword() {
        mBinding.btnChangePwd.setOnClickListener(view -> {
            Toast.makeText(mMainActivity, "Change pwd", Toast.LENGTH_SHORT).show();
        });
    }
}