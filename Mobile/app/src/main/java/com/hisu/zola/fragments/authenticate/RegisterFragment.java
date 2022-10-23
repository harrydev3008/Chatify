package com.hisu.zola.fragments.authenticate;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentRegisterBinding;
import com.hisu.zola.entity.User;
import com.hisu.zola.fragments.ConfirmOTPFragment;
import com.hisu.zola.util.dialog.ConfirmSendOTPDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding mBinding;
    private MainActivity mMainActivity;
    private User user;
    private ConfirmSendOTPDialog dialog;
    private Calendar mCalendar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);

        init();

        return mBinding.getRoot();
    }

    private void init() {
        user = new User();
        mCalendar = Calendar.getInstance();

        addToggleShowPasswordEvent(mBinding.edtPassword, mBinding.tvTogglePassword);
        addToggleShowPasswordEvent(mBinding.edtConfirmPassword, mBinding.tvToggleConfirmPassword);

        addChangeBackgroundColorOnFocusForUserNameEditText();
        addChangeBackgroundColorOnFocusForDisplayEditText();

        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtPassword, mBinding.linearLayout);
        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtConfirmPassword, mBinding.linearLayoutConfirm);

        addActionForEditTextDateOfBirth();
        addSwitchToLoginEvent();
        addActionForBtnRegister();
    }

    private void addActionForBtnRegister() {
        mBinding.btnRegister.setOnClickListener(view -> {
            register();
        });
    }

    private void register() {
        String phoneNo = mBinding.edtUsername.getText().toString().trim();
        String displayName = mBinding.edtDisplayName.getText().toString().trim();
        String pwd = mBinding.edtPassword.getText().toString().trim();
        String confirmPwd = mBinding.edtConfirmPassword.getText().toString().trim();

        if (validateUserRegisterAccount(phoneNo, displayName, pwd, confirmPwd)) {
            if(dialog == null)
                initDialog();

            dialog.setNewPhoneNumber(phoneNo);
            dialog.showDialog();
        }
    }

    private boolean validateUserRegisterAccount(String phoneNo, String displayName, String pwd, String confirmPwd) {
        //Todo: verify user info => Huy
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
//Todo: catch dob

//        if(mBinding.edtDob.getText().toString().length() != 1) {
//            mBinding.edtDob.setError(getString(R.string.not_match_confirm_pwd_err));
//            mBinding.edtDob.requestFocus();
//            return false;
//        }

        user.setPhoneNumber(phoneNo);
        user.setPassword(pwd);
        user.setUsername(displayName);

        return true;
    }

    private void initDialog() {
        dialog = new ConfirmSendOTPDialog(mMainActivity, Gravity.CENTER, getString(R.string.otp_change_phone_no));

        dialog.addActionForBtnChange(view_change -> {
            dialog.dismissDialog();
        });

        dialog.addActionForBtnConfirm(view_confirm -> {
            dialog.dismissDialog();
            mMainActivity.setFragment(ConfirmOTPFragment.newInstance(ConfirmOTPFragment.REGISTER_ARGS, user));
        });
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


    private void addActionForEditTextDateOfBirth() {
        mBinding.edtDob.setOnClickListener(view -> {

            Locale locale = new Locale("vi");
            Locale.setDefault(locale);

            DatePickerDialog datePickerDialog = new DatePickerDialog(mMainActivity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (datePicker, year, month, dayOfMonth) -> {
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.YEAR, year);
                        updateDateOfBirthEditText();
                    }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setTitle(getString(R.string.dob));
            datePickerDialog.setIcon(R.drawable.ic_calendar);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }

    private void updateDateOfBirthEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        mBinding.edtDob.setText(dateFormat.format(mCalendar.getTime()));
    }
}