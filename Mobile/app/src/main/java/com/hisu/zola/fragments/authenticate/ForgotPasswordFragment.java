package com.hisu.zola.fragments.authenticate;

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
import com.hisu.zola.databinding.FragmentForgotPasswordBinding;
import com.hisu.zola.fragments.ConfirmOTPFragment;
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

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding mBinding;
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
        mBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mainActivity.setProgressbarVisibility(View.GONE);
        backToPrevPage();
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtRegisPhoneNumber);
        EditTextUtil.toggleShowClearIconOnEditText(mainActivity, mBinding.edtRegisPhoneNumber);
        phoneNumberOnChangeEvent();
        addActionForBtnContinue();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void initDialog() {
        dialog = new ConfirmSendOTPDialog(mainActivity, Gravity.CENTER, getString(R.string.otp_change_phone_no));

        dialog.addActionForBtnChange(view_change -> {
            dialog.dismissDialog();
            mBinding.edtRegisPhoneNumber.requestFocus();
        });

        dialog.addActionForBtnConfirm(view_confirm -> {
            dialog.dismissDialog();
            mainActivity.addFragmentToBackStack(ConfirmOTPFragment.newInstance(ConfirmOTPFragment.FORGOT_PWD_ARGS, LocalDataManager.getCurrentUserInfo()));
        });
    }

    private void phoneNumberOnChangeEvent() {
        mBinding.edtRegisPhoneNumber.addTextChangedListener(new TextWatcher() {
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
                    mBinding.edtRegisPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(
                            null, null,
                            ContextCompat.getDrawable(mainActivity, R.drawable.ic_close), null
                    );
                else
                    mBinding.edtRegisPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null
                    );
            }
        });
    }

    private void addActionForBtnContinue() {
        mBinding.btnContinue.setOnClickListener(view -> {
            if (dialog == null)
                initDialog();

            if (verifyPhoneNumber(mBinding.edtRegisPhoneNumber.getText().toString())) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.showDialog();
                });

                JsonObject object = new JsonObject();
                object.addProperty("phoneNumber", mBinding.edtRegisPhoneNumber.getText().toString());
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

                        if (exist) {
                            dialog.setNewPhoneNumber(mBinding.edtRegisPhoneNumber.getText().toString());
                            dialog.showDialog();
                        } else {
                            mainActivity.runOnUiThread(() -> {
                                loadingDialog.dismissDialog();
                                new iOSDialogBuilder(mainActivity)
                                        .setTitle(getString(R.string.notification_warning))
                                        .setSubtitle(getString(R.string.phone_not_regis))
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

    /*
     * @author Huy
     * */
    private boolean verifyPhoneNumber(String phoneNumber) {
        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");

        if (!patternPhoneNumber.matcher(phoneNumber).matches()) {
            mBinding.edtRegisPhoneNumber.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtRegisPhoneNumber.requestFocus();
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