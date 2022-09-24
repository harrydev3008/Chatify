package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterBinding;

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
        String phoneNo = mBinding.edtUsername.getText().toString().trim();
        String pwd = mBinding.edtPassword.getText().toString().trim();
        String confirmPwd = mBinding.edtConfirmPassword.getText().toString().trim();

        if (validateUserRegisterAccount(phoneNo, pwd, confirmPwd)) {
            mMainActivity.setFragment(new HomeFragment());
        }
    }

    private boolean validateUserRegisterAccount(String phoneNo, String pwd, String confirmPwd) {

        if (TextUtils.isEmpty(phoneNo)) {
            mBinding.edtUsername.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtUsername.requestFocus();
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
}