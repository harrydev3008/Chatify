package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentSettingBinding;
import com.hisu.zola.fragments.authenticate.ResetPasswordFragment;
import com.hisu.zola.util.local.LocalDataManager;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentSettingBinding.inflate(inflater, container, false);

        mainActivity.setBottomNavVisibility(View.GONE);

        loadUserInfo();

        addActionForBtnBackToPrevPage();
        addActionForBtnLogout();
        addActionForBtnChangePwd();
        addActionForBtnChangePhoneNumber();

        return mBinding.getRoot();
    }

    private void loadUserInfo() {
        mBinding.tvPhoneNo.setText(LocalDataManager.getCurrentUserInfo().getPhoneNumber());
    }

    private void addActionForBtnLogout() {
        mBinding.tvLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirm))
                    .setPositiveButton(getString(R.string.logout),
                            (dialogInterface, i) -> mainActivity.logOut())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
    }

    private void addActionForBtnChangePwd() {
        mBinding.tvChangePassword.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mainActivity.getViewContainerID(), new ResetPasswordFragment())
                    .addToBackStack("change_pwd")
                    .commit();
        });
    }

    private void addActionForBtnChangePhoneNumber() {
        mBinding.acChangePhoneNumber.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new ConfirmChangePhoneNumberFragment());
        });
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}