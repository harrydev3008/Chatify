package com.hisu.zola.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentSettingBinding;
import com.hisu.zola.fragments.ResetPasswordFragment;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.OtpDialog;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentSettingBinding.inflate(inflater, container, false);

        mainActivity.setBottomNavVisibility(View.GONE);

        addActionForBtnBackToPrevPage();
        addActionForBtnLogout();
        addActionForBtnChangePwd();
        addActionForBtnChangePhoneNumber();
        addActionForBtnDeleteAccount();

        return mBinding.getRoot();
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
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.change_phone_no))
                    .setMessage(getString(R.string.reset_phone_confirm))
                    .setPositiveButton(getString(R.string.send_me_otp),
                            (dialogInterface, i) -> showConfirmResetPhoneNumberDialog())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
    }

    private void showConfirmResetPhoneNumberDialog() {
        OtpDialog otpDialog = new OtpDialog(mainActivity, Gravity.CENTER);

        otpDialog.addActionForBtnCancel(view -> {
            otpDialog.dismissDialog();
        });

        otpDialog.addActionForBtnConfirm(view -> {
            if (verifyOTP(otpDialog.getEdtOtp(), "123")) {
                otpDialog.dismissDialog();
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(mainActivity.getViewContainerID(), new ResetPasswordFragment())
                        .addToBackStack("change_pwd")
                        .commit();
            }
        });

        otpDialog.addActionForBtnReSentOtp(view -> {
            Toast.makeText(mainActivity, "OTP not receive!", Toast.LENGTH_SHORT).show();
        });

        otpDialog.showDialog();

        NotificationUtil.otpNotification(
                mainActivity, getString(R.string.system_noty_channel_id),
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

    private void addActionForBtnDeleteAccount() {
        mBinding.tvDeleteAccount.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "Delete account", Toast.LENGTH_SHORT).show();
        });
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}