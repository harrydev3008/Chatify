package com.hisu.zola.fragments.authenticate;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentLoginBinding;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.converter.ObjectConvertUtil;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;
    private MainActivity mMainActivity;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        init();

        return mBinding.getRoot();
    }

    private void init() {
        loadingDialog = new LoadingDialog(mMainActivity, Gravity.CENTER);

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForPasswordEditText();

        addToggleShowPasswordEvent();
        addSwitchToRegisterEvent();
        addActionForBtnLogin();
        addActionForBtnForgotPassword();

        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtPhoneNo);
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtPassword);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtPhoneNo);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtPassword);
    }

    private void addActionForBtnLogin() {
        mBinding.btnLogin.setOnClickListener(view -> {
            addLoginEvent();
        });
    }

    private void addActionForBtnForgotPassword() {
        mBinding.tvForgotPwd.setOnClickListener(view -> {
            //Todo: feature in progress
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
                    loadingDialog.showDialog();
                });

                User user = new User(phoneNumber, password);

                ApiService.apiService.signIn(user).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if (response.isSuccessful() && response.code() == 200) {

                            LocalDataManager.setUserLoginState(true);
                            LocalDataManager.setCurrentUserInfo(ObjectConvertUtil.getResponseUser(response));

                            mMainActivity.runOnUiThread(() -> {
                                loadingDialog.dismissDialog();
                                mMainActivity.setBottomNavVisibility(View.VISIBLE);
                                mMainActivity.setFragment(new ConversationListFragment());
                            });

                        } else if (response.code() == 400) {
                            try {
                                JsonObject obj = new Gson().fromJson(response.errorBody().string(), JsonObject.class);

                                int errorCode = obj.get("errorCode").getAsInt();
                                String errorMsg = obj.get("message").getAsString();

                                mMainActivity.runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
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

    /**
     * @author Huy
     */
    private boolean validateUserAccount(String phoneNumber, String password) {
        if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.edtPhoneNo.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtPhoneNo.requestFocus();
            return false;
        }

        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");

        Matcher matcher = patternPhoneNumber.matcher(phoneNumber);
        if (!matcher.matches()) {
            mBinding.edtPhoneNo.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtPhoneNo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.edtPassword.setError(getString(R.string.empty_pwd_err));
            mBinding.edtPassword.requestFocus();
            return false;
        }

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
}