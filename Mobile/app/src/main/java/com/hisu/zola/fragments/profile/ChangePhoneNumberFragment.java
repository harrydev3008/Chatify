package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentChangePhoneNumberBinding;
import com.hisu.zola.fragments.ConfirmOTPFragment;
import com.hisu.zola.fragments.authenticate.ForgotPasswordFragment;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.dialog.ConfirmSendOTPDialog;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePhoneNumberFragment extends Fragment {

    private FragmentChangePhoneNumberBinding mBinding;
    private MainActivity mainActivity;
    private ConfirmSendOTPDialog dialog;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentChangePhoneNumberBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backToPrevPage();
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        phoneNumberOnChangeEvent();
        EditTextUtil.toggleShowClearIconOnEditText(mainActivity, mBinding.edtNewPhoneNo);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtNewPhoneNo);
        addActionForBtnContinue();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void phoneNumberOnChangeEvent() {
        mBinding.edtNewPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.primary_color));
                    mBinding.btnContinue.setClickable(true);
                } else {
                    mBinding.btnContinue.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.gray_bf));
                    mBinding.btnContinue.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    mBinding.edtNewPhoneNo.setCompoundDrawablesWithIntrinsicBounds(
                            null, null,
                            ContextCompat.getDrawable(mainActivity, R.drawable.ic_close), null
                    );
                else
                    mBinding.edtNewPhoneNo.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null
                    );
            }
        });
    }

    private void initDialog() {
        dialog = new ConfirmSendOTPDialog(mainActivity, Gravity.CENTER, mainActivity.getString(R.string.otp_change_phone_no));

        dialog.addActionForBtnChange(view_change -> {
            dialog.dismissDialog();
            mBinding.edtNewPhoneNo.requestFocus();
        });

        dialog.addActionForBtnConfirm(view_confirm -> {
            dialog.dismissDialog();
            User user = LocalDataManager.getCurrentUserInfo();
            user.setPhoneNumber(mBinding.edtNewPhoneNo.getText().toString());
            mainActivity.addFragmentToBackStack(ConfirmOTPFragment.newInstance(ConfirmOTPFragment.CHANGE_PHONE_NO_ARGS, user));
        });
    }

    private void addActionForBtnContinue() {
        mBinding.btnContinue.setOnClickListener(view -> {
            if (dialog == null)
                initDialog();

            if (verifyPhoneNumber(mBinding.edtNewPhoneNo.getText().toString())) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.showDialog();
                });

                JsonObject object = new JsonObject();
                object.addProperty("phoneNumber", mBinding.edtNewPhoneNo.getText().toString());
                RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

                ApiService.apiService.checkUserExistByPhoneNumber(body).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        Gson gson = new Gson();

                        mainActivity.runOnUiThread(() -> {
                            loadingDialog.dismissDialog();
                        });

                        String json = gson.toJson(response.body());
                        JsonObject obj = gson.fromJson(json, JsonObject.class);

                        boolean exist = obj.get("isExist").getAsBoolean();

                        if (!exist) {
                            dialog.setNewPhoneNumber(mBinding.edtNewPhoneNo.getText().toString());
                            dialog.showDialog();
                        } else {
                            mainActivity.runOnUiThread(() -> {
                                loadingDialog.dismissDialog();
                                new iOSDialogBuilder(mainActivity)
                                        .setTitle(getString(R.string.notification_warning))
                                        .setSubtitle(getString(R.string.phone_registered))
                                        .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        mainActivity.runOnUiThread(() -> {
                            loadingDialog.dismissDialog();
                            new iOSDialogBuilder(mainActivity)
                                    .setTitle(getString(R.string.notification_warning))
                                    .setSubtitle(getString(R.string.notification_warning_msg))
                                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                        });
                        Log.e(ForgotPasswordFragment.class.getName(), t.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private boolean verifyPhoneNumber(String phoneNumber) {
        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|070|079|077|076|078|089|090|093|083|084|085|081|082|088|091|094|052|056|058|092|059|099|087)[0-9]{7}$");

        if (!patternPhoneNumber.matcher(phoneNumber).matches()) {
            mBinding.edtNewPhoneNo.setError(mainActivity.getString(R.string.invalid_phone_format_err));
            mBinding.edtNewPhoneNo.requestFocus();
            return false;
        }
        return true;
    }
}