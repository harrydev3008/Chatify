package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentProfileBinding;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.OtpDialog;

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
            new AlertDialog.Builder(mMainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.change_password))
                    .setMessage(getString(R.string.reset_pwd_confirm))
                    .setPositiveButton(getString(R.string.send_me_otp),
                            (dialogInterface, i) -> showConfirmResetPwDialog())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
    }

    private void showConfirmResetPwDialog() {
        OtpDialog otpDialog = new OtpDialog(mMainActivity, Gravity.CENTER);

        otpDialog.addActionForBtnCancel(view -> {
            otpDialog.dismissDialog();
        });

        otpDialog.addActionForBtnConfirm(view -> {
            if (verifyOTP(otpDialog.getEdtOtp(), "123")) {
                otpDialog.dismissDialog();
                mMainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(mMainActivity.getViewContainerID(), new ResetPasswordFragment())
                        .addToBackStack("change_pwd")
                        .commit();
            }
        });

        otpDialog.addActionForBtnReSentOtp(view -> {
            Toast.makeText(mMainActivity, "OTP not receive!", Toast.LENGTH_SHORT).show();
        });

        otpDialog.showDialog();

        NotificationUtil.otpNotification(
                mMainActivity, getString(R.string.system_noty_channel_id),
                getString(R.string.otp), "123"
        );
    }

    private boolean verifyOTP(EditText editText, String otpNumber) {

        if(TextUtils.isEmpty(editText.getText().toString().trim())) {
            editText.setError(getString(R.string.empty_otp_err));
            editText.requestFocus();
            return false;
        }

        if(!editText.getText().toString().trim().equals(otpNumber)) {
            editText.setError(getString(R.string.wrong_otp_err));
            editText.requestFocus();
            return false;
        }

        return true;
    }
}