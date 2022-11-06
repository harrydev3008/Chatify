package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentConfirmOtpBinding;
import com.hisu.zola.fragments.authenticate.RegisterFragment;
import com.hisu.zola.fragments.authenticate.RegisterUserInfoFragment;
import com.hisu.zola.fragments.authenticate.ResetPasswordFragment;
import com.hisu.zola.util.dialog.LoadingDialog;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfirmOTPFragment extends Fragment {

    private static final String OTP_ARGS = "OTP_ARGS";
    private static final String USER_ARGS = "USER_ARGS";
    public static final String REGISTER_ARGS = "REGISTER_ARGS";
    public static final String CHANGE_PHONE_NO_ARGS = "CHANGE_PHONE_NO_ARGS";
    public static final String FORGOT_PWD_ARGS = "FORGOT_PWD_ARGS";
    private String argument;
    private User user;
    private FirebaseAuth mAuth;
    private String verificationID = "";
    private LoadingDialog loadingDialog;
    private PhoneAuthProvider.ForceResendingToken token;

    private FragmentConfirmOtpBinding mBinding;
    private MainActivity mainActivity;

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

        mainActivity = (MainActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vi");

        if (getArguments() != null) {
            argument = getArguments().getString(OTP_ARGS);
            user = (User) getArguments().getSerializable(USER_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConfirmOtpBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);

        backToPrevPage();
        initOTPInput();
        addActionForBtnVerifyOTP();
        handleSendOTP();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            if (argument.equalsIgnoreCase(REGISTER_ARGS))
                mainActivity.setFragment(new RegisterFragment());
            else
                mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
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
                if (charSequence.length() > 0) {
                    mBinding.edtInputOtp4.setHint(mBinding.edtInputOtp4.getText());
                    mBinding.edtInputOtp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp3.requestFocus();
            }
        });

        mBinding.edtInputOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.edtInputOtp5.setHint(mBinding.edtInputOtp5.getText());
                    mBinding.edtInputOtp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp4.requestFocus();
            }
        });
        mBinding.edtInputOtp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    mBinding.edtInputOtp6.setHint(mBinding.edtInputOtp6.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1)
                    mBinding.edtInputOtp5.requestFocus();
            }
        });
    }

    private void addActionForBtnVerifyOTP() {
        mBinding.btnVerifyOtp.setOnClickListener(view -> {
            if (verifyOTP(getOtpInput())) {
                loadingDialog.showDialog();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, getOtpInput());
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private String getOtpInput() {
        return mBinding.edtInputOtp1.getText().toString() +
                mBinding.edtInputOtp2.getText().toString() +
                mBinding.edtInputOtp3.getText().toString() +
                mBinding.edtInputOtp4.getText().toString() +
                mBinding.edtInputOtp5.getText().toString() +
                mBinding.edtInputOtp6.getText().toString();
    }

    private boolean verifyOTP(String otp) {
        if (otp.length() < 6) {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(getString(R.string.notification_warning))
                    .setSubtitle(getString(R.string.empty_otp_err))
                    .setPositiveListener(getString(R.string.confirm), dialog -> {
                        clearOTPInput();
                        dialog.dismiss();
                    }).build().show();
            return false;
        }

        return true;
    }

    private void clearOTPInput() {
        mBinding.edtInputOtp1.setText("");
        mBinding.edtInputOtp2.setText("");
        mBinding.edtInputOtp3.setText("");
        mBinding.edtInputOtp4.setText("");
        mBinding.edtInputOtp5.setText("");
        mBinding.edtInputOtp6.setText("");
        mBinding.edtInputOtp1.requestFocus();
    }

    private void switchToNextPage() {
        switch (argument) {
            case REGISTER_ARGS:
                mainActivity.setFragment(RegisterUserInfoFragment.newInstance(user));
                break;
            case CHANGE_PHONE_NO_ARGS:
                new iOSDialogBuilder(mainActivity)
                        .setTitle(getString(R.string.notification_warning))
                        .setSubtitle(getString(R.string.change_phone_no_success))
                        .setPositiveListener(getString(R.string.confirm), dialog -> {
                            dialog.dismiss();
                            mainActivity.getSupportFragmentManager().popBackStackImmediate();
                            mainActivity.getSupportFragmentManager().popBackStackImmediate();
                            mainActivity.getSupportFragmentManager().popBackStackImmediate();
                        }).build().show();
                break;
            case FORGOT_PWD_ARGS:
                mainActivity.setFragment(ResetPasswordFragment.newInstance(ResetPasswordFragment.FORGOT_PWD_ARGS));

                break;
        }
    }

    private void handleSendOTP() {
        loadingDialog.showDialog();
        String userPhone = user.getPhoneNumber();
        String phoneNumber = "+84" + userPhone.substring(userPhone.indexOf("0") + 1);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(mainActivity)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                new iOSDialogBuilder(mainActivity)
                                        .setTitle(getString(R.string.otp_verification_err))
                                        .setSubtitle(e.getLocalizedMessage())
                                        .setCancelable(true)
                                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                loadingDialog.dismissDialog();
                                verificationID = verificationId;
                                startTime();
                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startTime() {
        mBinding.tvResend.setOnClickListener(null);
        mBinding.tvResend.setTextColor(mainActivity.getColor(R.color.gray));
        mBinding.tvResendTimeout.setVisibility(View.VISIBLE);

        DecimalFormat decimalFormat = new DecimalFormat("00");
        Timer timer = new Timer();
        AtomicInteger counter = new AtomicInteger(30);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter.decrementAndGet();
                String textPlaceholder = "00:" + decimalFormat.format(counter.get());
                mainActivity.runOnUiThread(() -> {
                    mBinding.tvResendTimeout.setText(textPlaceholder);
                });

                if (counter.get() == 0) {
                    timer.cancel();
                    mainActivity.runOnUiThread(() -> {
                        mBinding.tvResendTimeout.setVisibility(View.GONE);
                        addActionForTvResend();
                    });
                }
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void addActionForTvResend() {
        mBinding.tvResend.setTextColor(mainActivity.getColor(R.color.darkerBlue));
        mBinding.tvResend.setOnClickListener(view -> {
            resendOTP();
        });
    }

    private void resendOTP() {
        String userPhone = user.getPhoneNumber();
        String phoneNumber = "+84" + userPhone.substring(userPhone.indexOf("0") + 1);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(mainActivity)
                        .setForceResendingToken(token)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                new iOSDialogBuilder(mainActivity)
                                        .setTitle(getString(R.string.otp_verification_err))
                                        .setSubtitle(e.getLocalizedMessage())
                                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                verificationID = verificationId;
                                token = forceResendingToken;
                                startTime();
                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismissDialog();
                        if (task.isSuccessful()) {
                            switchToNextPage();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                new iOSDialogBuilder(mainActivity)
                                        .setTitle(getString(R.string.otp_verification_err))
                                        .setSubtitle(getString(R.string.wrong_otp_err))
                                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAuth = null;
    }
}