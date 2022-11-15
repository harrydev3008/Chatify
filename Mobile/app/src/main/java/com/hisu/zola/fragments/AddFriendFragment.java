package com.hisu.zola.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.google.gson.JsonObject;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentAddFriendBinding;
import com.hisu.zola.fragments.contact.FriendFromContactFragment;
import com.hisu.zola.fragments.contact.FriendRequestFragment;
import com.hisu.zola.util.network.ApiService;
import com.hisu.zola.util.network.Constraints;
import com.hisu.zola.util.network.NetworkUtil;
import com.hisu.zola.util.dialog.AddFriendDialog;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendFragment extends Fragment {

    private FragmentAddFriendBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentAddFriendBinding.inflate(inflater, container, false);

        mainActivity.setBottomNavVisibility(View.GONE);

        backToPrevPage();
        showFriendRequestList();
        showFriendFromContact();
        phoneNumberOnChangeEvent();
        addActionForBtnFind();

        return mBinding.getRoot();
    }

    private void showFriendRequestList() {
        mBinding.acFriendRequest.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendRequestFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showFriendFromContact() {
        mBinding.acPhoneContact.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendFromContactFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void phoneNumberOnChangeEvent() {
        mBinding.edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mBinding.btnFind.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.primary_color));
                    mBinding.btnFind.setClickable(true);
                } else {
                    mBinding.btnFind.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.gray_bf));
                    mBinding.btnFind.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void addActionForBtnFind() {
        mBinding.btnFind.setOnClickListener(view -> {
            if (verifyPhoneNumber(mBinding.edtPhoneNumber.getText().toString())) {
                if (NetworkUtil.isConnectionAvailable(mainActivity))
                    findFriend();
                else
                    new iOSDialogBuilder(mainActivity)
                            .setTitle(getString(R.string.no_network_connection))
                            .setSubtitle(getString(R.string.no_network_connection))
                            .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
            }
        });
    }

    private void findFriend() {
        JsonObject object = new JsonObject();
        object.addProperty("phoneNumber", mBinding.edtPhoneNumber.getText().toString());
        RequestBody body = RequestBody.create(MediaType.parse(Constraints.JSON_TYPE), object.toString());

        ApiService.apiService.findFriendByPhoneNumber(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    User foundUser = response.body();

                    if (foundUser != null) {
                        mainActivity.runOnUiThread(() -> {
                            AddFriendDialog dialog = new AddFriendDialog(mainActivity, Gravity.CENTER, foundUser);
                            dialog.showDialog();
                        });
                    } else {
                        mainActivity.runOnUiThread(() -> {
                            new iOSDialogBuilder(mainActivity)
                                    .setTitle(getString(R.string.notification_warning))
                                    .setSubtitle(getString(R.string.user_not_found))
                                    .setPositiveListener(getString(R.string.confirm), iOSDialog::dismiss).build().show();
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(AddFriendFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    /**
     * @author Huy
     */
    private boolean verifyPhoneNumber(String phoneNumber) {
        Pattern patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|" +
                "070|079|077|076|078|089|090|093|" +
                "083|084|085|081|082|088|091|094|" +
                "056|058|092|" +
                "059|099)[0-9]{7}$");

        if (!patternPhoneNumber.matcher(phoneNumber).matches()) {
            mBinding.edtPhoneNumber.setError(getString(R.string.invalid_phone_format_err));
            mBinding.edtPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }
}