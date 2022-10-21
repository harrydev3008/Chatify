package com.hisu.zola.fragments.authenticate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentLoginBinding;
import com.hisu.zola.entity.User;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.NotificationUtil;
import com.hisu.zola.util.ObjectConvertUtil;
import com.hisu.zola.util.OtpDialog;
import com.hisu.zola.util.local.LocalDataManager;


import java.io.IOException;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;
    private MainActivity mMainActivity;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        init();

        return mBinding.getRoot();
    }

    private void init() {
        progressDialog = new ProgressDialog(mMainActivity);

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForPasswordEditText();

        addToggleShowPasswordEvent();
        addSwitchToRegisterEvent();
        addActionForBtnLogin();
        addActionForBtnForgotPassword();
    }

    private void addActionForBtnLogin() {
        mBinding.btnLogin.setOnClickListener(view -> {
            addLoginEvent();
        });
    }

    private void addActionForBtnForgotPassword() {
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
    }

    private void addChangeBackgroundColorOnFocusForUserNameEditText() {
        mBinding.edtPhoneNo.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                mBinding.edtPhoneNo.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                mBinding.edtPhoneNo.setBackground(
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
        String phoneNumber = mBinding.edtPhoneNo.getText().toString();
        String password = mBinding.edtPassword.getText().toString();

        if (validateUserAccount(phoneNumber, password)) {

            Executors.newSingleThreadExecutor().execute(() -> {

                mMainActivity.runOnUiThread(() -> {
                    progressDialog.setMessage(getString(R.string.pls_wait));
                    progressDialog.show();
                });

                User user = new User(phoneNumber, password);

                ApiService.apiService.signIn(user).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if (response.isSuccessful() && response.code() == 200) {

                            LocalDataManager.setUserLoginState(true);
                            LocalDataManager.setCurrentUserInfo(ObjectConvertUtil.getResponseUser(response));

                            mMainActivity.runOnUiThread(() -> {
                                progressDialog.dismiss();
                                mMainActivity.setBottomNavVisibility(View.VISIBLE);
                                mMainActivity.setFragment(new ConversationListFragment());
                            });

                        } else if (response.code() == 400) {
                            try {
                                JsonObject obj = new Gson().fromJson(response.errorBody().string(), JsonObject.class);

                                int errorCode = obj.get("errorCode").getAsInt();
                                String errorMsg = obj.get("message").getAsString();

                                mMainActivity.runOnUiThread(() -> {
                                    progressDialog.dismiss();
                                    handleLoginError(errorCode, errorMsg);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        Log.e("API_ERR", t.getLocalizedMessage());
                    }
                });
            });
        }
    }

    private boolean validateUserAccount(String phoneNumber, String password) {
        //Todo: check phone number and password before calling api to verify user => Huy => done
        if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.edtPhoneNo.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtPhoneNo.requestFocus();
            return false;
        }
        //kiểm tra regex sdt
        Pattern patternsdt = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");
        Matcher matcher = patternsdt.matcher(phoneNumber);
        if (!matcher.matches()){
            mBinding.edtPhoneNo.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtPhoneNo.requestFocus();
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

    private void handleLoginError(int errorCode, String errorMessage) {
        if (errorCode == 1) {
            mBinding.edtPhoneNo.setError(errorMessage);
            mBinding.edtPhoneNo.requestFocus();
            return;
        }

        mBinding.edtPassword.setError(getString(R.string.wrong_pwd_err));
        mBinding.edtPassword.requestFocus();
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

        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            editText.setError(getString(R.string.empty_otp_err));
            editText.requestFocus();
            return false;
        }

        if (!editText.getText().toString().trim().equals(otpNumber)) {
            editText.setError(getString(R.string.wrong_otp_err));
            editText.requestFocus();
            return false;
        }

        return true;
    }
}