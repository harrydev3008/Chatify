package com.hisu.zola.fragments.authenticate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterBinding;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.OtpDialog;

import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);

        addToggleShowPasswordEvent(mBinding.edtPassword, mBinding.tvTogglePassword);
        addToggleShowPasswordEvent(mBinding.edtConfirmPassword, mBinding.tvToggleConfirmPassword);

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForDisplayEditText();

        addChangeBackgroundColorOnFocusForPasswordEditText(
                mBinding.edtPassword, mBinding.linearLayout
        );

        addChangeBackgroundColorOnFocusForPasswordEditText(
                mBinding.edtConfirmPassword, mBinding.linearLayoutConfirm
        );

        addSwitchToLoginEvent();

        mBinding.btnRegister.setOnClickListener(view -> {
            register();
        });

        return mBinding.getRoot();
    }

    private void register() {
        ProgressDialog progressDialog = new ProgressDialog(mMainActivity);

        Executors.newSingleThreadExecutor().execute(() -> {

            mMainActivity.runOnUiThread(() -> {
                progressDialog.setMessage(getString(R.string.pls_wait));
                progressDialog.show();
            });

            String phoneNo = mBinding.edtUsername.getText().toString().trim();
            String displayName = mBinding.edtDisplayName.getText().toString().trim();
            String pwd = mBinding.edtPassword.getText().toString().trim();
            String confirmPwd = mBinding.edtConfirmPassword.getText().toString().trim();

            if (validateUserRegisterAccount(phoneNo, displayName, pwd, confirmPwd)) {
                openConfirmOTPDialog(Gravity.CENTER);
            }
        });
    }

    private boolean validateUserRegisterAccount(String phoneNo, String displayName, String pwd, String confirmPwd) {

        if (TextUtils.isEmpty(phoneNo)) {
            mBinding.edtUsername.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(displayName)) {
            mBinding.edtDisplayName.setError(getString(R.string.empty_display_name_err));
            mBinding.edtDisplayName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(pwd)) {
            mBinding.edtPassword.setError(getString(R.string.empty_pwd_err));
            mBinding.edtPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            mBinding.edtConfirmPassword.setError(getString(R.string.empty_confirm_pwd_err));
            mBinding.edtConfirmPassword.requestFocus();
            return false;
        }

        if (!pwd.equals(confirmPwd)) {
            mBinding.edtConfirmPassword.setError(getString(R.string.not_match_confirm_pwd_err));
            mBinding.edtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void openConfirmOTPDialog(int gravity) {
        OtpDialog otpDialog = new OtpDialog(mMainActivity, Gravity.CENTER);

        otpDialog.addActionForBtnCancel(view -> {
            otpDialog.dismissDialog();
        });

        otpDialog.addActionForBtnConfirm(view -> {
            if (verifyOTP(otpDialog.getEdtOtp(), "123")) {
                otpDialog.dismissDialog();
                mMainActivity.setFragment(new RegisterUserInfoFragment());
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

    private void addChangeBackgroundColorOnFocusForDisplayEditText() {
        mBinding.edtDisplayName.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                mBinding.edtDisplayName.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                mBinding.edtDisplayName.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addChangeBackgroundColorOnFocusForPasswordEditText(EditText edtPwd,
                                                                    LinearLayout layout) {

        edtPwd.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                layout.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                layout.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addToggleShowPasswordEvent(EditText edtPwd, TextView tvTogglePwd) {
        String showText = getString(R.string.show);
        String hideText = getString(R.string.hide);

        tvTogglePwd.setOnClickListener(view -> {

            if (tvTogglePwd.getText().toString().equalsIgnoreCase(showText)) {
                tvTogglePwd.setText(hideText);
                edtPwd.setTransformationMethod(null);
            } else {
                tvTogglePwd.setText(showText);
                edtPwd.setTransformationMethod(new PasswordTransformationMethod());
            }

            edtPwd.setSelection(edtPwd.getText().length());
        });
    }

    private void addSwitchToLoginEvent() {
        mBinding.tvSwitchToRegister.setOnClickListener(view -> {
            mMainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(mMainActivity.getViewContainerID(), new LoginFragment())
                    .commit();
        });
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