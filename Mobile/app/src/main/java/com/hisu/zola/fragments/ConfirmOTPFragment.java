package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentConfirmOtpBinding;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.fragments.authenticate.RegisterUserInfoFragment;

public class ConfirmOTPFragment extends Fragment {

    private static final String OTP_ARGS = "OTP_ARGS";
    private static final String USER_ARGS = "USER_ARGS";
    public static final String REGISTER_ARGS = "REGISTER_ARGS";
    public static final String CHANGE_PHONE_NO_ARGS = "CHANGE_PHONE_NO_ARGS";
    private String argument;
    private User user;

    private FragmentConfirmOtpBinding mBinding;
    private MainActivity mainActivity;
    private AlertDialog dialog;

    public static ConfirmOTPFragment newInstance(String argsParam, User user) {
        Bundle args = new Bundle();
        args.putString(OTP_ARGS, argsParam);
        args.putSerializable(USER_ARGS, user);
        ConfirmOTPFragment fragment = new ConfirmOTPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            argument = getArguments().getString(OTP_ARGS);
            user = (User) getArguments().getSerializable(USER_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentConfirmOtpBinding.inflate(inflater, container, false);

        initOTPInput();
        addActionForBtnVerifyOTP();

        return mBinding.getRoot();
    }

    private void initOTPInput() {
        mBinding.edtInputOtp1.requestFocus();

        mBinding.edtInputOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.edtInputOtp1.setHint(mBinding.edtInputOtp1.getText());
                    mBinding.edtInputOtp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mBinding.edtInputOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.edtInputOtp2.setHint(mBinding.edtInputOtp2.getText());
                    mBinding.edtInputOtp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp1.requestFocus();
            }
        });

        mBinding.edtInputOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.edtInputOtp3.setHint(mBinding.edtInputOtp3.getText());
                    mBinding.edtInputOtp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp2.requestFocus();
            }
        });

        mBinding.edtInputOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    mBinding.edtInputOtp4.setHint(mBinding.edtInputOtp4.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp3.requestFocus();
            }
        });
    }

    private void addActionForBtnVerifyOTP() {
        mBinding.btnVerifyOtp.setOnClickListener(view -> {
            if (verifyOTP(getOtpInput()))
                switchToNextPage();
        });
    }

    private String getOtpInput() {
        return mBinding.edtInputOtp1.getText().toString() +
                mBinding.edtInputOtp2.getText().toString() +
                mBinding.edtInputOtp3.getText().toString() +
                mBinding.edtInputOtp4.getText().toString();
    }

    private boolean verifyOTP(String otp) {

        if (otp.length() < 4) {
            showAlertDialog(getString(R.string.empty_otp_err));
            return false;
        }

        //Todo: verify otp via api
        return true;
    }

    private void showAlertDialog(String message) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.ic_alert)
                    .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                        clearOTPInput();
                        mBinding.edtInputOtp1.requestFocus();
                    })
                    .create();
        }

        dialog.setMessage(message);
        dialog.show();
    }

    private void clearOTPInput() {
        mBinding.edtInputOtp1.setText("");
        mBinding.edtInputOtp2.setText("");
        mBinding.edtInputOtp3.setText("");
        mBinding.edtInputOtp4.setText("");
    }

    private void switchToNextPage() {
        if (argument.equals(REGISTER_ARGS)) {
            mainActivity.setFragment(RegisterUserInfoFragment.newInstance(user));
        } else if (argument.equals(CHANGE_PHONE_NO_ARGS)) {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        }
    }
}