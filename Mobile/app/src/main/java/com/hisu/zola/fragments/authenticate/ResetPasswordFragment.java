package com.hisu.zola.fragments.authenticate;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentResetPasswordBinding;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    public static final String RESET_PWD_ARGS = "RESET_PWD_ARGS";
    public static final String FORGOT_PWD_ARGS = "FORGOT_PWD_ARGS";
    private static final String USER_OPTION_ARGS = "USER_OPTIONS";

    private FragmentResetPasswordBinding mBinding;
    private MainActivity mainActivity;
    private UserRepository userRepository;
    private String arguments;

    public static ResetPasswordFragment newInstance(String argsValue) {
        Bundle args = new Bundle();
        args.putString(USER_OPTION_ARGS, argsValue);
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            arguments = getArguments().getString(USER_OPTION_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(mainActivity.getApplication());

        if(arguments.equalsIgnoreCase(RESET_PWD_ARGS))
            mBinding.linearLayout3.setVisibility(View.VISIBLE);
        else
            mBinding.linearLayout3.setVisibility(View.GONE);

        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtNewPwd, mBinding.linearLayout);
        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtConfirmNewPwd, mBinding.linearLayout2);
        addChangeBackgroundColorOnFocusForPasswordEditText(mBinding.edtOldPwd, mBinding.linearLayout3);

        addToggleShowPasswordEvent(mBinding.tvTogglePassword, mBinding.edtNewPwd);
        addToggleShowPasswordEvent(mBinding.tvToggleConfirmPassword, mBinding.edtConfirmNewPwd);
        addToggleShowPasswordEvent(mBinding.tvToggleOldPassword, mBinding.edtOldPwd);

        EditTextUtil.toggleShowClearIconOnEditText(mainActivity, mBinding.edtOldPwd);
        EditTextUtil.toggleShowClearIconOnEditText(mainActivity, mBinding.edtNewPwd);
        EditTextUtil.toggleShowClearIconOnEditText(mainActivity, mBinding.edtConfirmNewPwd);

        EditTextUtil.clearTextOnSearchEditText(mBinding.edtOldPwd);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtNewPwd);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtConfirmNewPwd);

        addActionForBtnBack();
        addActionForBtnSaveChangePwd();
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
                    mBinding.edtConfirmNewPwd.getText().toString().trim())) {
                if (NetworkUtil.isConnectionAvailable(mainActivity))
                    updateUserInfo(mBinding.edtNewPwd.getText().toString().trim());
            }
        });
    }

    private void updateUserInfo(String newPwd) {

        JsonObject object = new JsonObject();
        object.addProperty("password", newPwd);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        ApiService.apiService.changePassword(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if(response.isSuccessful() && response.code() == 200) {
                    Gson gson = new Gson();

                    String json = gson.toJson(response.body());
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    User updatedUser = gson.fromJson(obj.get("existingUser"), User.class);
                    userRepository.update(updatedUser);
                    LocalDataManager.setCurrentUserInfo(updatedUser);

                    mainActivity.runOnUiThread(() -> {
                        new iOSDialogBuilder(mainActivity)
                                .setTitle(getString(R.string.notification_warning))
                                .setSubtitle(getString(R.string.reset_password_success))
                                .setPositiveListener(getString(R.string.confirm), dialog -> {
                                    dialog.dismiss();
                                    switchToNextPage();
                                }).build().show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ResetPasswordFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void switchToNextPage() {
        switch (arguments) {
            case RESET_PWD_ARGS:
                mainActivity.getSupportFragmentManager().popBackStackImmediate();
                break;

            case FORGOT_PWD_ARGS:
                mainActivity.setFragment(new LoginFragment());
                break;
        }
    }

    /**
     * @author Huy
     */
    private boolean validateNewPassword(String newPwd, String confirmPwd) {
        if (TextUtils.isEmpty(newPwd)) {
            mBinding.edtNewPwd.setError(getString(R.string.empty_pwd_err));
            mBinding.edtNewPwd.requestFocus();
            return false;
        }

        Pattern patternPwd = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?])[A-Za-z\\d@$!%*?]{8,}$");
        if (!patternPwd.matcher(newPwd).matches()) {
            mBinding.edtNewPwd.setError(getString(R.string.invalid_pwd_format_err));
            mBinding.edtNewPwd.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            mBinding.edtConfirmNewPwd.setError(getString(R.string.empty_confirm_pwd_err));
            mBinding.edtConfirmNewPwd.requestFocus();
            return false;
        }

        if (!newPwd.equals(confirmPwd)) {
            mBinding.edtConfirmNewPwd.setError(getString(R.string.not_match_confirm_pwd_err));
            mBinding.edtConfirmNewPwd.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}