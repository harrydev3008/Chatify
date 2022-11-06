package com.hisu.zola.fragments.authenticate;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentRegisterBinding;
import com.hisu.zola.fragments.ConfirmOTPFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.util.dialog.ConfirmSendOTPDialog;
import com.hisu.zola.util.dialog.LoadingDialog;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding mBinding;
    private MainActivity mMainActivity;
    private User user;
    private ConfirmSendOTPDialog dialog;
    private Calendar mCalendar;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        user = new User();
        mCalendar = Calendar.getInstance();
        loadingDialog = new LoadingDialog(mMainActivity, Gravity.CENTER);

        addToggleShowPasswordEvent(mBinding.edtPassword, mBinding.tvTogglePassword);
        addToggleShowPasswordEvent(mBinding.edtConfirmPassword, mBinding.tvToggleConfirmPassword);

        addChangeBackgroundColorOnFocusForPhoneNumberEditText();
        addChangeBackgroundColorOnFocusForDisplayEditText();

        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtPassword, mBinding.linearLayout);
        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtConfirmPassword, mBinding.linearLayoutConfirm);

        addActionForEditTextDateOfBirth();
        addSwitchToLoginEvent();
        addActionForBtnRegister();

        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtPhoneNumber);
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtDisplayName);
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtPassword);
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtConfirmPassword);

        EditTextUtil.clearTextOnSearchEditText(mBinding.edtPhoneNumber);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtDisplayName);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtPassword);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtConfirmPassword);
    }

    private void addActionForBtnRegister() {
        mBinding.btnRegister.setOnClickListener(view -> {
            if (NetworkUtil.isConnectionAvailable(mMainActivity))
                register();
            else {
                new iOSDialogBuilder(mMainActivity)
                        .setTitle(getString(R.string.no_network_connection))
                        .setSubtitle(getString(R.string.no_network_connection_desc))
                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void register() {
        String phoneNo = mBinding.edtPhoneNumber.getText().toString().trim();
        String displayName = mBinding.edtDisplayName.getText().toString().trim();
        String pwd = mBinding.edtPassword.getText().toString().trim();
        String confirmPwd = mBinding.edtConfirmPassword.getText().toString().trim();

        if (validateUserRegisterAccount(phoneNo, displayName, pwd, confirmPwd)) {
            saveUserInfo();
        }
    }

    private void saveUserInfo() {
        Executors.newSingleThreadExecutor().execute(() -> {

            mMainActivity.runOnUiThread(() -> {
                loadingDialog.showDialog();
            });

            mMainActivity.runOnUiThread(() -> {
                loadingDialog.dismissDialog();

                if (dialog == null)
                    initDialog();

                dialog.setNewPhoneNumber(user.getPhoneNumber());
                dialog.showDialog();
            });

            JsonObject object = new JsonObject();
            object.addProperty("phoneNumber", mBinding.edtPhoneNumber.getText().toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            ApiService.apiService.findFriendByPhoneNumber(body).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        User foundUser = response.body();
                        if (foundUser != null) {
                            mMainActivity.runOnUiThread(() -> {
                                mBinding.edtPhoneNumber.setError(getString(R.string.registered_phone_err));
                                mBinding.edtPhoneNumber.requestFocus();
                            });
                        } else {
                            mMainActivity.runOnUiThread(() -> {
                                if (dialog == null)
                                    initDialog();

                                dialog.setNewPhoneNumber(user.getPhoneNumber());
                                dialog.showDialog();
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e(RegisterFragment.class.getName(), t.getLocalizedMessage());
                }
            });
        });
    }

    /**
     * @author Huy
     */
    private boolean validateUserRegisterAccount(String phoneNo, String displayName, String pwd, String confirmPwd) {
        if (TextUtils.isEmpty(phoneNo)) {
            mBinding.edtPhoneNumber.setError(getString(R.string.empty_phone_no_err));
            mBinding.edtPhoneNumber.requestFocus();
            return false;
        }

        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");

        if (!patternPhoneNumber.matcher(phoneNo).matches()) {
            mBinding.edtPhoneNumber.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtPhoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(displayName)) {
            mBinding.edtDisplayName.setError(getString(R.string.empty_display_name_err));
            mBinding.edtDisplayName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mBinding.edtDob.getText().toString())) {
            new iOSDialogBuilder(mMainActivity)
                    .setTitle(getString(R.string.notification_warning))
                    .setSubtitle(getString(R.string.empty_dob_err))
                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            return false;
        }

        if (calculateAge(mBinding.edtDob.getText().toString()) < 15) {
            new iOSDialogBuilder(mMainActivity)
                    .setTitle(getString(R.string.notification_warning))
                    .setSubtitle(getString(R.string.err_age))
                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            return false;
        }

        if (TextUtils.isEmpty(pwd)) {
            mBinding.edtPassword.setError(getString(R.string.empty_pwd_err));
            mBinding.edtPassword.requestFocus();
            return false;
        }

        Pattern patternPwd = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?])[A-Za-z\\d@$!%*?]{8,}$");
        if (!patternPwd.matcher(pwd).matches()) {
            mBinding.edtPassword.setError(getString(R.string.invalid_pwd_format_err));
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

        user.setPhoneNumber(phoneNo);
        user.setPassword(pwd);
        user.setUsername(displayName);
        user.setDob(getDobFormat());

        return true;
    }

    private String getDobFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(mCalendar.getTime());
    }

    private static int calculateAge(String dobStr) {

        String[] dates = dobStr.trim().split("/");

        Date dob = new Date(
                Integer.parseInt(dates[2]),
                Integer.parseInt(dates[1]),
                Integer.parseInt(dates[0])
        );

        LocalDate today = LocalDate.now();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dob);

        int age = today.getYear() - birthDate.get(Calendar.YEAR) + 1900;
        if (((birthDate.get(Calendar.MONTH)) > today.getMonthValue())) {
            age--;
        } else if ((birthDate.get(Calendar.MONTH) == today.getMonthValue()) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.getDayOfMonth())) {
            age--;
        }

        return age;
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

    private void addChangeBackgroundColorOnFocusForPhoneNumberEditText() {
        mBinding.edtPhoneNumber.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus)
                mBinding.edtPhoneNumber.setBackground(
                        ContextCompat.getDrawable(mMainActivity.getApplicationContext(),
                                R.drawable.edit_text_outline_focus));
            else
                mBinding.edtPhoneNumber.setBackground(
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
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.confirm), datePickerDialog);
            datePickerDialog.show();
        });
    }

    private void updateDateOfBirthEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        mBinding.edtDob.setText(dateFormat.format(mCalendar.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}