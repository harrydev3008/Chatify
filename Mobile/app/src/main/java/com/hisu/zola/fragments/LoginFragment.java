package com.hisu.zola.fragments;

import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentLoginBinding;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.OtpDialog;
import com.hisu.zola.util.local.LocalDataManager;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForPasswordEditText();

        addToggleShowPasswordEvent();
        addSwitchToRegisterEvent();

        mBinding.btnLogin.setOnClickListener(view -> {
            addLoginEvent();
        });

        mBinding.tvForgotPwd.setOnClickListener(view -> {
            new AlertDialog.Builder(mMainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle(getString(R.string.change_password))
                    .setMessage(getString(R.string.reset_phone_confirm))
                    .setPositiveButton(getString(R.string.send_me_otp),
                            (dialogInterface, i) -> showConfirmResetPwDialog())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

        return mBinding.getRoot();
    }

    private void addChangeBackgroundColorOnFocusForUserNameEditText() {
        mBinding.edtUsername.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                mBinding.edtUsername.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                mBinding.edtUsername.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addChangeBackgroundColorOnFocusForPasswordEditText() {
        mBinding.edtPassword.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                mBinding.linearLayout.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                mBinding.linearLayout.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addToggleShowPasswordEvent() {
        String showText = getString(R.string.show);
        String hideText = getString(R.string.hide);

        mBinding.tvTogglePassword.setOnClickListener(view -> {

            if (mBinding.tvTogglePassword.getText().toString().equalsIgnoreCase(showText)) {
                mBinding.tvTogglePassword.setText(hideText);
                mBinding.edtPassword.setTransformationMethod(null);
            } else {
                mBinding.tvTogglePassword.setText(showText);
                mBinding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
            }

            mBinding.edtPassword.setSelection(mBinding.edtPassword.getText().length());
        });
    }

    private void addSwitchToRegisterEvent() {
        mBinding.tvSwitchToRegister.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(mMainActivity.getViewContainerID(), new RegisterFragment())
                    .commit();
        });
    }

    private void addLoginEvent() {
        String username = mBinding.edtUsername.getText().toString();
        String password = mBinding.edtPassword.getText().toString();

        if (validateUserAccount(username, password)) {
            LocalDataManager.setUserLoginState(true);
            mMainActivity.setBottomNavVisibility(View.VISIBLE);
            mMainActivity.setFragment(new ConversationListFragment());
        }
    }

    private boolean validateUserAccount(String username, String password) {

        if (TextUtils.isEmpty(username)) {
            mBinding.edtUsername.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.edtPassword.setError(getString(R.string.empty_pwd_err));
            mBinding.edtPassword.requestFocus();
            return false;
        }

//      Todo: Validate user info goes here

        return true;
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
                        .addToBackStack("reset_pwd")
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