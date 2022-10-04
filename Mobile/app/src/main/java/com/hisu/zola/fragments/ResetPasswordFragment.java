package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentResetPasswordBinding;

public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        mBinding = FragmentResetPasswordBinding.inflate(inflater, container, false);

        addChangeBackgroundColorOnFocusForPasswordEditText(
                mBinding.edtNewPwd,
                mBinding.linearLayout
        );

        addChangeBackgroundColorOnFocusForPasswordEditText(
                mBinding.edtConfirmNewPwd,
                mBinding.linearLayout2
        );

        addToggleShowPasswordEvent(
                mBinding.tvTogglePassword,
                mBinding.edtNewPwd
        );

        addToggleShowPasswordEvent(
                mBinding.tvToggleConfirmPassword,
                mBinding.edtConfirmNewPwd
        );

        addActionForBtnBack();
        addActionForBtnSaveChangePwd();

        return mBinding.getRoot();
    }

    private void addChangeBackgroundColorOnFocusForPasswordEditText(
            EditText editText,
            LinearLayout linearLayout
    ) {
        editText.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                linearLayout.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                linearLayout.setBackground(
                        ContextCompat.getDrawable(mainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline));
        });
    }

    private void addToggleShowPasswordEvent(TextView tvTogglePwd, EditText editText) {
        String showText = getString(R.string.show);
        String hideText = getString(R.string.hide);

        tvTogglePwd.setOnClickListener(view -> {

            if (tvTogglePwd.getText().toString().equalsIgnoreCase(showText)) {
                tvTogglePwd.setText(hideText);
                editText.setTransformationMethod(null);
            } else {
                tvTogglePwd.setText(showText);
                editText.setTransformationMethod(new PasswordTransformationMethod());
            }

            editText.setSelection(editText.getText().length());
        });
    }

    private void addActionForBtnBack() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStack();
        });
    }

    private void addActionForBtnSaveChangePwd() {
        mBinding.btnSave.setOnClickListener(view -> {
            if (validateNewPassword(mBinding.edtNewPwd.getText().toString().trim(),
                    mBinding.edtConfirmNewPwd.getText().toString().trim()))
                mainActivity.setFragment(HomeFragment.newInstance(HomeFragment.NORMAL_ARGS));
        });
    }

    private boolean validateNewPassword(String newPwd, String confirmPwd) {

        if(TextUtils.isEmpty(newPwd)) {
            mBinding.edtNewPwd.setError(getString(R.string.empty_pwd_err));
            mBinding.edtNewPwd.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(confirmPwd)) {
            mBinding.edtConfirmNewPwd.setError(getString(R.string.empty_confirm_pwd_err));
            mBinding.edtConfirmNewPwd.requestFocus();
            return false;
        }

        if(!newPwd.equals(confirmPwd)) {
            mBinding.edtConfirmNewPwd.setError(getString(R.string.not_match_confirm_pwd_err));
            mBinding.edtConfirmNewPwd.requestFocus();
            return false;
        }

        return true;
    }
}