package com.hisu.zola.fragments.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.UserRepository;
import com.hisu.zola.databinding.FragmentSettingBinding;
import com.hisu.zola.fragments.authenticate.ResetPasswordFragment;
import com.hisu.zola.util.dialog.LoadingDialog;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding mBinding;
    private MainActivity mainActivity;
    private UserRepository repository;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(mainActivity, Gravity.CENTER);
        mainActivity.setBottomNavVisibility(View.GONE);
        repository = new UserRepository(mainActivity.getApplication());

        loadUserInfo();
        addActionForBtnBackToPrevPage();
        addActionForBtnLogout();
        addActionForBtnChangePwd();
        addActionForBtnChangePhoneNumber();
        addActionForSwitchEnableVerifyOtp();
    }

    private void addActionForSwitchEnableVerifyOtp() {
        mBinding.swEnableVerifyTwoStep.setOnClickListener(view -> {
            if (!mBinding.swEnableVerifyTwoStep.isChecked()) {
                new iOSDialogBuilder(mainActivity)
                        .setTitle(mainActivity.getString(R.string.turn_off_otp_verify))
                        .setSubtitle(mainActivity.getString(R.string.turn_off_otp_verify_desc))
                        .setPositiveListener(mainActivity.getString(R.string.confirm), dialog -> {
                            dialog.dismiss();
                            mBinding.swEnableVerifyTwoStep.setChecked(false);
                            disableVerifyOTP(false);
                        })
                        .setNegativeListener(mainActivity.getString(R.string.cancel), dialog -> {
                            dialog.dismiss();
                            mBinding.swEnableVerifyTwoStep.setChecked(true);
                        }).build().show();
            } else {
                disableVerifyOTP(true);
            }
        });
    }

    private void disableVerifyOTP(boolean verify) {
        mainActivity.runOnUiThread(() -> {
            loadingDialog.showDialog();
        });

        JsonObject object = new JsonObject();
        object.addProperty("isVerifyOtp", verify);
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());
        ApiService.apiService.changeVerifyOtpState(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    mainActivity.runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                    });

                    User updated = response.body();
                    if (updated != null)
                        repository.update(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                mainActivity.runOnUiThread(() -> {
                    loadingDialog.showDialog();
                });
                Log.e(SettingFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void loadUserInfo() {
        User localUser = LocalDataManager.getCurrentUserInfo();
        if (localUser != null)
            repository.getUser(localUser.getId()).observe(mainActivity, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user == null) return;
                    mBinding.tvPhoneNo.setText(user.getPhoneNumber());
                    mBinding.swEnableVerifyTwoStep.setChecked(user.isVerifyOTP());
                }
            });
    }

    private void addActionForBtnLogout() {
        mBinding.tvLogout.setOnClickListener(view -> {
            new iOSDialogBuilder(mainActivity)
                    .setTitle(mainActivity.getString(R.string.logout))
                    .setSubtitle(mainActivity.getString(R.string.logout_confirm))
                    .setPositiveListener(mainActivity.getString(R.string.logout), dialog -> {
                        dialog.dismiss();
                        mainActivity.logOut();
                    })
                    .setNegativeListener(mainActivity.getString(R.string.cancel), iOSDialog::dismiss).build().show();
        });
    }

    private void addActionForBtnChangePwd() {
        mBinding.tvChangePassword.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(ResetPasswordFragment.newInstance(ResetPasswordFragment.RESET_PWD_ARGS));
        });
    }

    private void addActionForBtnChangePhoneNumber() {
        mBinding.acChangePhoneNumber.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new ConfirmChangePhoneNumberFragment());
        });
    }

    private void addActionForBtnBackToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}